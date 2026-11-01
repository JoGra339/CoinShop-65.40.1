package de.coinshop.gui;

import de.coinshop.CoinShopPlugin;
import de.coinshop.ItemUtil;
import de.coinshop.Messages;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Das Hauptmenü des Schwarzmarkts (nur mit der Schwarzmarktlizenz). Oben die Grundfunktionen, darunter die Bereiche,
 * die erst mit der jeweiligen Lizenz erscheinen (Drogenhandel, Sklavenhandel).
 */
public final class BlackMarketHubMenu extends Menu {

    private final CoinShopPlugin pl;

    @Override
    public void open(Player player) {
        if (!pl.blackMarket().unlocked(player)) {
            pl.messages().send(player, "bm-locked");
            return; // ohne Schwarzmarktlizenz kommt man nicht hinein
        }
        super.open(player);
    }

    public BlackMarketHubMenu(CoinShopPlugin plugin, Player viewer) {
        super(plugin, 4, plugin.messages().get("gui.bmhub.title"));
        this.pl = plugin;
        Messages msg = plugin.messages();
        List<net.kyori.adventure.text.Component> none = List.of();

        // Grundfunktionen
        set(10, ItemUtil.icon(Material.WITHER_SKELETON_SKULL, msg.get("gui.bmhub.shop"), none),
                (p, c) -> new BlackMarketMenu(plugin, p).open(p));
        set(11, ItemUtil.icon(Material.GOLD_INGOT, msg.get("gui.bmhub.revier"), none),
                (p, c) -> new ReviereMenu(plugin, p).open(p));
        set(12, ItemUtil.icon(Material.WRITTEN_BOOK, msg.get("gui.bmhub.members"), none),
                (p, c) -> new BlackMarketMembersMenu(plugin, p).open(p));
        set(14, ItemUtil.icon(Material.ENCHANTED_BOOK, msg.get("gui.bmhub.licenses"), none),
                (p, c) -> new LicenseMenu(plugin, p, true).open(p));

        // Bereiche, die erst mit ihrer Lizenz auftauchen
        int slot = 20;
        if (plugin.drugs().enabled() && plugin.licenses().has(viewer.getUniqueId(), "drogenhandel")) {
            set(slot++, ItemUtil.icon(Material.BREWING_STAND, msg.get("gui.bmhub.drugs"), none),
                    (p, c) -> new DrugHubMenu(plugin, p).open(p));
        }
        if (plugin.creatureAuction().enabled() && plugin.licenses().has(viewer.getUniqueId(), "sklavenhandel")) {
            set(slot++, ItemUtil.icon(Material.LEAD, msg.get("gui.bmhub.beasts"), none),
                    (p, c) -> new CreatureAuctionMenu(plugin, p).open(p));
        }
        set(27, backButton(), (p, c) -> new MainMenu(plugin).open(p));
        set(35, balanceIcon(viewer), incomeClick());
        fillEmpty();
    }
}
