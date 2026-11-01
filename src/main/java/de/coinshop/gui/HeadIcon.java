package de.coinshop.gui;

import de.coinshop.ItemUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.List;
import java.util.UUID;

/** Spielerkopf als Menü-Symbol. */
public final class HeadIcon {

    private HeadIcon() {
    }

    public static ItemStack head(UUID owner, Component name, List<Component> lore) {
        ItemStack item = ItemUtil.icon(Material.PLAYER_HEAD, name, lore);
        OfflinePlayer off = Bukkit.getOfflinePlayer(owner);
        item.editMeta(meta -> {
            if (meta instanceof SkullMeta skull) {
                skull.setOwningPlayer(off);
            }
        });
        return item;
    }
}
