package moe.seikimo.droplet.inventory;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class DropletInventory implements Inventory {
    private final List<Item> contents = new ArrayList<>();
}
