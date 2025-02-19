package mapleglory.database;

import mapleglory.server.guild.Guild;
import mapleglory.server.guild.GuildRanking;

import java.util.List;
import java.util.Optional;

public interface GuildAccessor {
    Optional<Guild> getGuildById(int guildId);

    boolean checkGuildNameAvailable(String name);

    boolean newGuild(Guild guild);

    boolean saveGuild(Guild guild);

    boolean deleteGuild(int guildId);

    List<GuildRanking> getGuildRankings();
}