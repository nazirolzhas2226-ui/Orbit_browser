const {
  initializeTestEnvironment,
  assertFails,
  assertSucceeds,
} = require("@firebase/rules-unit-testing");
const { test, before, after, beforeEach } = require("node:test");
const fs = require("node:fs");

let testEnv;
const PROJECT_ID = process.env.GCP_PROJECT || "demo-no-project";
const ALICE_UID = "alice_orbit_123";
const BOB_UID = "bob_orbit_456";

const [emulatorHost, emulatorPortStr] = (process.env.FIRESTORE_EMULATOR_HOST || "127.0.0.1:8085").split(":");
const emulatorPort = parseInt(emulatorPortStr, 10);

before(async () => {
  const rules = fs.readFileSync("./firestore.rules", "utf8");
  testEnv = await initializeTestEnvironment({
    projectId: PROJECT_ID,
    firestore: {
      rules,
      host: emulatorHost,
      port: emulatorPort,
    },
  });
});

after(async () => {
  if (testEnv) {
    await testEnv.cleanup();
  }
});

beforeEach(async () => {
  if (testEnv) {
    await testEnv.clearFirestore();
  }
});

function buildSettingsPayload(uid, now) {
  return {
    userId: uid,
    styleMode: "LIQUID_GLASS",
    themeBrightness: "SYSTEM",
    colorPalette: "ORBIT_CYAN",
    wallpaperStyle: "NONE",
    homeLayout: "CLASSIC",
    showFavorites: true,
    showPrivacyReport: true,
    showRedAiBar: true,
    showGamingTelemetry: false,
    adBlockEnabled: true,
    antiTrackingEnabled: true,
    httpsOnlyEnabled: true,
    vpnEnabled: false,
    vpnRegion: "AMSTERDAM",
    antivirusProvider: "KASPERSKY",
    gamingRgbEnabled: true,
    gamingHapticsEnabled: true,
    performanceModeEnabled: false,
    ramLimitMb: 2048,
    tabHibernationEnabled: true,
    searchEngine: "GOOGLE",
    appLanguage: "RU",
    redAiAutoSummarize: false,
    redAiSafeLinkScan: true,
    redAiStyle: "CONCISE",
    notifyBlockedTrackers: true,
    notifyCloudSync: true,
    notifySecurityAlerts: true,
    adsBlockedTotal: 0,
    trackersBlockedTotal: 0,
    createdAt: now,
    updatedAt: now,
  };
}

test("Unauthenticated user cannot read or write any Orbit data", async () => {
  const unauthDb = testEnv.unauthenticatedContext().firestore();
  const now = new Date(Date.now() - 1000);
  await assertFails(unauthDb.collection("users").doc(ALICE_UID).get());
  await assertFails(
    unauthDb.collection("users").doc(ALICE_UID).collection("favorites").get()
  );
  await assertFails(
    unauthDb.collection("users").doc(ALICE_UID).collection("settings").doc("orbit_config").set(
      buildSettingsPayload(ALICE_UID, now)
    )
  );
});

test("Authenticated owner can create and update NAccount profile, rejects ghost fields and cross-user writes", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const bobDb = testEnv.authenticatedContext(BOB_UID).firestore();
  const now = new Date(Date.now() - 2000);

  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).set({
      userId: ALICE_UID,
      naccountHandle: "@orbit_alice",
      displayName: "Alice Orbit",
      email: "alice@example.com",
      linkedProviders: ["GOOGLE", "TELEGRAM"],
      telegramHandle: "@alice_tg",
      cloudSyncEnabled: true,
      syncBookmarks: true,
      syncSettings: true,
      syncHistory: true,
      createdAt: now,
      updatedAt: now,
    })
  );

  // Partial update preserving createdAt
  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).update({
      vkHandle: "vk.com/alice",
      linkedProviders: ["GOOGLE", "TELEGRAM", "VK"],
      updatedAt: new Date(Date.now() - 500),
    })
  );

  // Shadow update with ghost field must fail
  await assertFails(
    aliceDb.collection("users").doc(ALICE_UID).update({
      isAdmin: true,
      updatedAt: new Date(Date.now() - 500),
    })
  );

  // Immutable createdAt mutation must fail
  await assertFails(
    aliceDb.collection("users").doc(ALICE_UID).update({
      createdAt: new Date(Date.now() - 100),
      updatedAt: new Date(Date.now() - 100),
    })
  );

  // Bob cannot read or modify Alice's NAccount
  await assertFails(bobDb.collection("users").doc(ALICE_UID).get());
  await assertFails(
    bobDb.collection("users").doc(ALICE_UID).update({ displayName: "Hacked" })
  );
});

test("Orbit settings create, partial update, enum validation, and bounds", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const now = new Date(Date.now() - 3000);
  const settingsRef = aliceDb.collection("users").doc(ALICE_UID).collection("settings").doc("orbit_config");

  await assertSucceeds(settingsRef.set(buildSettingsPayload(ALICE_UID, now)));

  await assertSucceeds(
    settingsRef.update({
      styleMode: "GAMING_GX",
      wallpaperStyle: "CYBER_GRID",
      ramLimitMb: 1024,
      updatedAt: new Date(Date.now() - 500),
    })
  );

  // New interface modes + Theme Studio fields + 6 languages + MercuryAI models
  await assertSucceeds(
    settingsRef.update({
      styleMode: "NOTHING_OS",
      colorPalette: "MERCURY_QUICKSILVER",
      wallpaperStyle: "JUPITER_VORTEX",
      iconTheme: "JUPITER_GX",
      appLanguage: "KK",
      mercuryAiModel: "gemini-3.8-flash",
      soundEffectsEnabled: false,
      customAccentHex: 0xFFFF3B30,
      customCornerRadiusDp: 12,
      borderGlowIntensity: 85,
      dnsOverHttpsEnabled: true,
      floatingSpeedHudEnabled: true,
      updatedAt: new Date(Date.now() - 450),
    })
  );

  // Invalid mercuryAiModel must fail
  await assertFails(
    settingsRef.update({
      mercuryAiModel: "unsupported-model-x",
      updatedAt: new Date(Date.now() - 420),
    })
  );

  // Invalid enum or out-of-range RAM limit must fail
  await assertFails(
    settingsRef.update({
      styleMode: "INVALID_MODE",
      updatedAt: new Date(Date.now() - 400),
    })
  );
  await assertFails(
    settingsRef.update({
      ramLimitMb: 64,
      updatedAt: new Date(Date.now() - 400),
    })
  );
});

test("Favorites, History, and Synced Tabs CRUD and cross-user isolation", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const bobDb = testEnv.authenticatedContext(BOB_UID).firestore();
  const now = new Date(Date.now() - 2000);

  // Favorites
  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).collection("favorites").doc("fav_google").set({
      userId: ALICE_UID,
      title: "Google",
      url: "https://www.google.com",
      iconBadge: "GOOGLE",
      accentHex: 0xFF4285F4,
      sortOrder: 0,
      createdAt: now,
      updatedAt: now,
    })
  );
  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).collection("favorites").get()
  );
  await assertFails(
    bobDb.collection("users").doc(ALICE_UID).collection("favorites").get()
  );

  // Reject @DocumentId "id" field in payload
  await assertFails(
    aliceDb.collection("users").doc(ALICE_UID).collection("favorites").doc("fav_bad").set({
      id: "fav_bad",
      userId: ALICE_UID,
      title: "Bad",
      url: "https://example.com",
      iconBadge: "WEB",
      accentHex: 0xFF000000,
      sortOrder: 1,
      createdAt: now,
      updatedAt: now,
    })
  );

  // History
  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).collection("history").doc("hist_1").set({
      userId: ALICE_UID,
      title: "Apple",
      url: "https://www.apple.com",
      visitedAt: now,
    })
  );
  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).collection("history").get()
  );

  // Synced Tabs
  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).collection("tabs").doc("tab_1").set({
      userId: ALICE_UID,
      title: "Orbit Start",
      url: "orbit://home",
      incognito: false,
      sortOrder: 0,
      createdAt: now,
      updatedAt: now,
    })
  );
  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).collection("tabs").doc("tab_1").update({
      title: "Wikipedia",
      url: "https://wikipedia.org",
      updatedAt: new Date(Date.now() - 400),
    })
  );
});
