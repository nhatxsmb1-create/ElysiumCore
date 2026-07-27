package dev.elysium.core.achievement;

/**
 * Dinh nghia tat ca achievement trong game.
 * Them achievement moi: them vao enum nay.
 */
public enum AchievementType {

    // ── Gia nhap ──────────────────────────────────────────────────────────────
    FIRST_JOIN      ("&aLan Dau Tien",      "&7Tham gia server lan dau tiên",         "&a"),
    PLAY_1H         ("&7Nguoi Moi",         "&7Choi 1 gio tren server",               "&7"),
    PLAY_24H        ("&eNguoi Yeu Server",  "&7Choi tong cong 24 gio",               "&e"),

    // ── Level ─────────────────────────────────────────────────────────────────
    LEVEL_5         ("&7Cap Do 5",          "&7Dat Level 5",                          "&7"),
    LEVEL_10        ("&aCap Do 10",         "&7Dat Level 10",                         "&a"),
    LEVEL_25        ("&bCap Do 25",         "&7Dat Level 25",                         "&b"),
    LEVEL_50        ("&dCap Do 50",         "&7Dat Level 50",                         "&d"),
    LEVEL_100       ("&6&lHuyen Thoai",     "&7Dat Level 100 - Dinh cao tu luyen!",   "&6"),

    // ── Kinh te ───────────────────────────────────────────────────────────────
    BALANCE_5000    ("&7Tiet Kiem",         "&7Kiem duoc 5,000 coins",               "&7"),
    BALANCE_50000   ("&aPhu Nho",           "&7Kiem duoc 50,000 coins",              "&a"),
    BALANCE_500000  ("&6Dai Gia",           "&7Kiem duoc 500,000 coins",             "&6"),
    BALANCE_5000000 ("&6&lTy Phu",          "&7Kiem duoc 5,000,000 coins!",          "&6"),

    // ── Class ─────────────────────────────────────────────────────────────────
    CLASS_CHOSEN    ("&dChon Con Duong",    "&7Chon class lan dau",                  "&d"),

    // ── Island ────────────────────────────────────────────────────────────────
    ISLAND_CREATED  ("&aDao Truong",        "&7Tao dao lan dau tien",                "&a");

    private final String displayName;
    private final String description;
    private final String color;

    AchievementType(String displayName, String description, String color) {
        this.displayName = displayName;
        this.description = description;
        this.color       = color;
    }

    public String getDisplayName() { return displayName; }
    public String getDescription() { return description; }
    public String getColor()       { return color; }
    public String getId()          { return name().toLowerCase(); }
}
