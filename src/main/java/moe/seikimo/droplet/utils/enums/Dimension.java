package moe.seikimo.droplet.utils.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Dimension {
    OVERWORLD("overworld", 0),
    NETHER("nether", 1),
    END("the_end", 2);

    final String name;
    final int id;
}
