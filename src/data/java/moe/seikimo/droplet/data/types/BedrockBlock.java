package moe.seikimo.droplet.data.types;

import org.cloudburstmc.nbt.NbtMap;

public record BedrockBlock(
        String prefixedName,
        int networkId,
        NbtMap states
) {
    /**
     * Creates a new bedrock block from the given NBT map.
     *
     * @param nbt The NBT map.
     * @return A new bedrock block.
     */
    public static BedrockBlock fromNbt(NbtMap nbt) {
        // Sort states by key name.
        var states = nbt.getCompound("states");
        var sortedStates = NbtMap.builder();
        states.keySet().stream()
                .sorted(String::compareTo)
                .forEach(key -> sortedStates.put(key, states.get(key)));

        return new BedrockBlock(
                nbt.getString("name"),
                nbt.getInt("network_id"),
                nbt.getCompound("states")
        );
    }

    /**
     * @return The name of the block.
     */
    public String name() {
        return prefixedName.split(":")[1];
    }

    @Override
    public String toString() {
        var builder = new StringBuilder(this.prefixedName());
        if (!this.states().isEmpty()) {
            builder.append('[');
            this.states().forEach((key, value) -> builder
                    .append(key).append('=').append(value).append(','));
            builder.deleteCharAt(builder.length() - 1);
            builder.append(']');
        }

        return builder.toString();
    }

    @Override
    public int hashCode() {
        return this.toString().hashCode();
    }
}
