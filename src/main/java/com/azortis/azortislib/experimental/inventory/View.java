/*
 * An open source utilities library used for Azortis plugins.
 *     Copyright (C) 2019  Azortis
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.azortis.azortislib.experimental.inventory;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

/**
 * An interface which represents what a user would see.
 */
@SuppressWarnings("unused")
public interface View extends InventoryHolder {
    /**
     * Get the parent page of this view.
     *
     * @return the Page which holds this view.
     */
    Page getPage();

    /**
     * Clears all references to the parent page and/or any other references outside of the View.
     */
    void dispose();

    /**
     * Sets the inventory linked to the View.
     *
     * @param inventory the {@link Inventory} to set it to.
     */
    void setInventory(Inventory inventory);
}
