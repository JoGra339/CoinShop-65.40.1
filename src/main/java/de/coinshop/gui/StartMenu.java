package de.coinshop.gui;

import de.coinshop.CoinShopPlugin;
import de.coinshop.ItemUtil;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Das Einsteiger-Menü (/start): drei erste Schritte. Wer noch keine Lizenz hat, sieht beim Öffnen des Hauptmenüs
 * zuerst dieses Menü. Über den Knopf unten rechts werden alle Bereiche freigegeben (merkt sich der Shop pro Spieler).
 */
public final class StartMenu extends Menu {

    public static boolean beginner(CoinShopPlugin plugin, Player player) {
        return plugin.database().getMetaLong("menu_full_" + player.getUniqueId(), 0) == 0
                && plugin.licenses().owned(player.getUniqueId()).isEmpty();
    }

    public StartMenu(CoinShopPlugin plugin, Player viewer) {
        super(plugin, 4, ExpeditionMenu.line("Willkommen im Shop", NamedTextColor.GOLD));

        set(11, ItemUtil.icon(Material.ENDER_CHEST, ExpeditionMenu.line("1. Etwas kaufen oder verkaufen", NamedTextColor.GOLD), List.of(
                ExpeditionMenu.line("Im Server-Shop kaufst du Items für Coins,", NamedTextColor.GRAY),
                ExpeditionMenu.line("und verkaufst, was du übrig hast.", NamedTextColor.GRAY),
                ExpeditionMenu.line("Klicken: Shop öffnen", NamedTextColor.GREEN))),
                (p, c) -> new ShopHubMenu(plugin, p).open(p));

        set(13, ItemUtil.icon(Material.WRITABLE_BOOK, ExpeditionMenu.line("2. Tagesaufgaben erledigen", NamedTextColor.GOLD), List.of(
                ExpeditionMenu.line("Jeden Tag neue Aufgaben mit Coins als Belohnung.", NamedTextColor.GRAY),
                ExpeditionMenu.line("Klicken: Aufgaben öffnen", NamedTextColor.GREEN))),
                (p, c) -> new QuestMenu(plugin, p).open(p));

        set(15, ItemUtil.icon(Material.PAPER, ExpeditionMenu.line("3. Eine Lizenz kaufen", NamedTextColor.GOLD), List.of(
                ExpeditionMenu.line("Lizenzen schalten Bereiche frei, zum Beispiel", NamedTextColor.GRAY),
                ExpeditionMenu.line("die Börse oder die Expeditionen.", NamedTextColor.GRAY),
                ExpeditionMenu.line("Klicken: Lizenzen ansehen", NamedTextColor.GREEN))),
                (p, c) -> new LicenseMenu(plugin, p).open(p));

        set(27, balanceIcon(viewer), incomeClick());
        set(31, ItemUtil.icon(Material.BARRIER, plugin.messages().get("gui.close-name"), List.of()),
                (p, c) -> p.closeInventory());
        set(35, ItemUtil.icon(Material.COMPASS, ExpeditionMenu.line("Alle Bereiche anzeigen", NamedTextColor.YELLOW), List.of(
                ExpeditionMenu.line("Zeigt das volle Hauptmenü mit allem,", NamedTextColor.GRAY),
                ExpeditionMenu.line("was es im Shop gibt.", NamedTextColor.GRAY),
                ExpeditionMenu.line("Klicken: Hauptmenü", NamedTextColor.GREEN))),
                (p, c) -> {
                    plugin.database().setMetaLong("menu_full_" + p.getUniqueId(), 1);
                    new MainMenu(plugin).open(p);
                });
        fillEmpty();
    }
}
