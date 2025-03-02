package mapleglory.provider;

import mapleglory.provider.mob.MobTemplate;
import mapleglory.provider.wz.*;
import mapleglory.provider.wz.property.WzListProperty;
import mapleglory.server.ServerConfig;
import mapleglory.server.ServerConstants;
import mapleglory.util.Tuple;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

public final class MobProvider implements WzProvider {
    public static final Path MOB_WZ = Path.of(ServerConfig.WZ_DIRECTORY, "Mob.wz");
    private static final Map<Integer, MobTemplate> mobTemplates = new HashMap<>();
    private static final Map<Integer, Set<Integer>> questCountGroups = new HashMap<>();

    public static void initialize() {
        try (final WzReader reader = WzReader.build(MOB_WZ, new WzReaderConfig(WzConstants.WZ_GMS_IV, ServerConstants.GAME_VERSION))) {
            final WzPackage wzPackage = reader.readPackage();
            loadMobTemplates(wzPackage);
            loadQuestCountGroups(wzPackage);
        } catch (IOException | ProviderError e) {
            throw new IllegalArgumentException("Exception caught while loading Mob.wz", e);
        }
    }

    public static Optional<MobTemplate> getMobTemplate(int mobId) {
        return Optional.ofNullable(mobTemplates.get(mobId));
    }

    public static Set<Integer> getQuestCountGroup(int mobId) {
        return questCountGroups.getOrDefault(mobId, Set.of());
    }

    private static void loadMobTemplates(WzPackage source) throws ProviderError {
        final Map<Integer, WzListProperty> mobProperties = new HashMap<>(); // mobId -> mobProperty
        final Map<Integer, Tuple<Integer, WzListProperty>> linkedMobs = new HashMap<>(); // mobId -> link, infoProp
        for (var mobEntry : source.getDirectory().getImages().entrySet()) {
            final int mobId = Integer.parseInt(mobEntry.getKey().replace(".img", ""));
            final WzListProperty mobProperty = mobEntry.getValue().getProperty();
            if (!(mobEntry.getValue().getProperty().get("info") instanceof WzListProperty infoProp)) {
                throw new ProviderError("Failed to resolve info property");
            }
            if (infoProp.getItems().containsKey("link")) {
                linkedMobs.put(mobId, Tuple.of(WzProvider.getInteger(infoProp.get("link")), infoProp));
                continue;
            }
            mobProperties.put(mobId, mobProperty);
            mobTemplates.put(mobId, MobTemplate.from(mobId, mobProperty, infoProp));
        }
        // Process linked mobs
        for (var linkEntry : linkedMobs.entrySet()) {
            final int mobId = linkEntry.getKey();
            int link = linkEntry.getValue().getLeft();
            while (!mobProperties.containsKey(link)) {
                // Client seems to be able to resolve recursive links for mobs, but not for other data
                link = linkedMobs.get(link).getLeft();
            }
            final WzListProperty linkProp = mobProperties.get(link);
            if (linkProp == null) {
                throw new ProviderError("Failed to resolve linked mob ID : %d, link : %d", mobId, link);
            }
            final WzListProperty infoProp = linkEntry.getValue().getRight();
            mobTemplates.put(mobId, MobTemplate.from(mobId, linkProp, infoProp));
        }
        // Validate mobs
        for (MobTemplate mobTemplate : mobTemplates.values()) {
            for (int reviveId : mobTemplate.getRevives()) {
                if (getMobTemplate(reviveId).isEmpty()) {
                    throw new ProviderError("Failed to resolve revive ID : %d for mob template ID : %d", reviveId, mobTemplate.getId());
                }
            }
        }
    }

    private static void loadQuestCountGroups(WzPackage source) throws ProviderError {
        final WzDirectory directory = source.getDirectory().getDirectories().get("QuestCountGroup");
        if (directory == null) {
            throw new ProviderError("Could not resolve Mob.wz/QuestCountGroup");
        }
        for (var groupEntry : directory.getImages().entrySet()) {
            final int mobId = Integer.parseInt(groupEntry.getKey().replace(".img", ""));
            if (!(groupEntry.getValue().getProperty().get("info") instanceof WzListProperty infoProp)) {
                throw new ProviderError("Failed to resolve info property");
            }
            final Set<Integer> group = new HashSet<>();
            for (var entry : infoProp.getItems().entrySet()) {
                group.add(WzProvider.getInteger(entry.getValue()));
            }
            questCountGroups.put(mobId, Collections.unmodifiableSet(group));
        }
    }
}
