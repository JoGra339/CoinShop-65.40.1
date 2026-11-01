package de.coinshop.gui;

import de.coinshop.CoinShopPlugin;
import de.coinshop.ItemUtil;
import de.coinshop.Messages;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.function.Consumer;

/**
 * Das Hauptmenü, in vier Reihen geordnet:
 * oben das Wichtigste (Shop, Aufgaben, Casino, Lizenzen), darunter Geld und Abenteuer, dann Profil und Info.
 * Bereiche, für die man eine Lizenz braucht (Großhandel, Expeditionen, Tiefenmine, Schwarzmarkt), erscheinen erst,
 * wenn man die Lizenz besitzt. Wer noch nichts besitzt, sieht zuerst das Einsteiger-Menü.
 */
public final class MainMenu extends Menu {

    private static final List<Component> NONE = List.of();
    private static final int[] MIDDLE = {19, 20, 21, 22, 23, 24, 25};

    private boolean built;
    private int next;

    public MainMenu(CoinShopPlugin plugin) {
        super(plugin, 5, plugin.messages().get("gui.main.title"));
    }

    private void middle(Material icon, String name, String line1, String line2, Consumer<Player> action) {
        if (next >= MIDDLE.length) {
            return;
        }
        set(MIDDLE[next++], ItemUtil.icon(icon, ExpeditionMenu.line(name, NamedTextColor.GOLD), List.of(
                ExpeditionMenu.line(line1, NamedTextColor.GRAY),
                ExpeditionMenu.line(line2, NamedTextColor.GRAY),
                ExpeditionMenu.line("Klicken: öffnen", NamedTextColor.GREEN))), (p, c) -> action.accept(p));
    }

    private void build(Player player) {
        Messages msg = plugin.messages();
        var licenses = plugin.licenses();
        var uuid = player.getUniqueId();

        // Reihe 2: das Wichtigste
        set(10, ItemUtil.icon(Material.ENDER_CHEST, msg.get("gui.main.shop-name"), NONE),
                (p, click) -> new ShopHubMenu(plugin, p).open(p));
        set(12, ItemUtil.icon(Material.WRITABLE_BOOK, msg.get("gui.main.quest-name"), NONE),
                (p, click) -> new QuestMenu(plugin, p).open(p));
        set(14, ItemUtil.icon(Material.NETHER_STAR, msg.get("gui.main.casino-name"), NONE),
                (p, click) -> new CasinoMenu(plugin, p).open(p));
        set(16, ItemUtil.icon(Material.PAPER, ExpeditionMenu.line("Lizenzen", NamedTextColor.GOLD), List.of(
                ExpeditionMenu.line("Schalte neue Bereiche frei.", NamedTextColor.GRAY),
                ExpeditionMenu.line("Klicken: öffnen", NamedTextColor.GREEN))),
                (p, click) -> new LicenseMenu(plugin, p).open(p));

        // Reihe 3: Geld und Abenteuer (gesperrte Bereiche fehlen, bis man die Lizenz hat)
        middle(Material.GOLD_BLOCK, "Investitionen", "Item-Sets für tägliches Einkommen,", "mit Börsenlizenz auch Aktien und Krypto.",
                p -> new InvestHubMenu(plugin, p).open(p));
        if (licenses.has(uuid, "grosshandel")) {
            middle(Material.BARREL, "Großhandel", "Aufträge, Branchen, Gesellschafter.", "Dein Gewerbe und deine Firma.",
                    p -> plugin.wholesale().openFor(p));
        }
        if (plugin.expeditions().enabled() && licenses.has(uuid, "expedition")) {
            middle(Material.DIAMOND_SWORD, "Expeditionen", "Level, Verteidigung, Begleiter,", "Ausrüstung und Training.",
                    p -> new ExpeditionMenu(plugin, p).open(p));
        }
        if (plugin.expeditions().enabled() && plugin.expeditions().mineEnabled() && licenses.has(uuid, "bergbau")) {
            middle(Material.IRON_PICKAXE, "Tiefenmine", "Erze abbauen, Ebene für Ebene tiefer.", "Kostet Ausdauer.",
                    p -> new MineMenu(plugin, p).open(p));
        }
        if (plugin.blackMarket().unlocked(player)) {
            middle(Material.WITHER_SKELETON_SKULL, "Schwarzmarkt", "Billige Ware, Reviere, Routen", "und schmutzige Geschäfte.",
                    p -> new BlackMarketHubMenu(plugin, p).open(p));
        }

        // Reihe 4: Ich und Info
        set(30, ItemUtil.icon(Material.PLAYER_HEAD, ExpeditionMenu.line("Ich und Fortschritt", NamedTextColor.GOLD), List.of(
                ExpeditionMenu.line("Profil, Titel, Ranglisten, Sammlungen, Erfolge", NamedTextColor.GRAY),
                ExpeditionMenu.line("Klicken: öffnen", NamedTextColor.GREEN))),
                (p, click) -> new AreaMenu(plugin, p, AreaMenu.Area.ME).open(p));
        set(32, ItemUtil.icon(Material.BELL, ExpeditionMenu.line("Info und Konto", NamedTextColor.GOLD), List.of(
                ExpeditionMenu.line("Ereignisse, Einkommen, Buchhaltung, Zeitplan", NamedTextColor.GRAY),
                ExpeditionMenu.line("Klicken: öffnen", NamedTextColor.GREEN))),
                (p, click) -> new AreaMenu(plugin, p, AreaMenu.Area.INFO).open(p));

        // Untere Reihe
        set(39, balanceIcon(player), incomeClick());
        set(40, ItemUtil.icon(Material.BARRIER, msg.get("gui.close-name"), NONE), (p, click) -> p.closeInventory());
        set(41, ItemUtil.icon(Material.GOLD_INGOT, msg.get("gui.main.shop-account-name"),
                msg.getList("gui.main.shop-account-lore", "coins", plugin.database().getShopAccount())), null);
        fillEmpty();
    }

    @Override
    public void open(Player player) {
        if (StartMenu.beginner(plugin, player)) {
            new StartMenu(plugin, player).open(player); // Einsteiger sehen zuerst nur drei Schritte
            return;
        }
        if (!built) {
            built = true;
            build(player);
        }
        super.open(player);
    }
}
