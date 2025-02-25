package xyz.acrylicstyle.mcutil;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;
import util.ICollectionList;
import xyz.acrylicstyle.mcutil.lang.CavesAndCliffsDataPack;
import xyz.acrylicstyle.mcutil.lang.MCVersion;
import xyz.acrylicstyle.mcutil.lang.ServerExe;
import xyz.acrylicstyle.mcutil.lang.ServerZip;

import java.util.List;
import java.util.Objects;

@RunWith(Parameterized.class)
public class AllMCVersionTest {
    public static final ICollectionList<MCVersion> data = ICollectionList.asList(MCVersion.values());

    @Parameters
    public static List<MCVersion> data() {
        return data;
    }

    @Parameter public MCVersion version;

    @Test
    public void ensureNoNameDupe() {
        if (data.filter(v -> v.getName().equals(version.getName())).size() > 1) {
            throw new AssertionError("There are 2 or more duplicates of: " + summarize(version));
        }
        data.filter(v -> v != version).forEach(v -> {
            if (version.getClientJars() != null) {
                for (String s : version.getClientJars()) {
                    if (contains(v.getClientJars(), Objects.requireNonNull(s))) throw new AssertionError("Duplicate client jar: " + s + " @ " + version.name());
                }
            }
            if (version.getServerJars() != null) {
                for (String s : version.getServerJars()) {
                    if (contains(v.getServerJars(), Objects.requireNonNull(s))) throw new AssertionError("Duplicate server jar: " + s + " @ " + version.name());
                }
            }
            if (version.<String[]>getValueOf(ServerExe.class) != null) {
                for (String s : Objects.requireNonNull(version.<String[]>getValueOf(ServerExe.class))) {
                    if (contains(v.getValueOf(ServerExe.class), Objects.requireNonNull(s))) throw new AssertionError("Duplicate server exe: " + s + " @ " + version.name());
                }
            }
            // duplicate client/server mappings is completely fine
        });
    }

    @Test
    public void ensureAlVersionsDoesNotThrowException() {
        version.getAlVersions();
    }

    @Test
    public void ensureCorrespondingVersionExists() {
        version.getCorrespondingVersion();
    }

    @Test
    public void ensureDownloadsAreInProperFormat() {
        endsWith("client json", version.getClientJsons(), version.isCombatTest() ? ".zip" : ".json");
        if (version != MCVersion.SNAPSHOT_12W19A && version != MCVersion.SNAPSHOT_12W18A) {
            endsWith("client jar", version.getClientJars(), ".jar");
        }
        endsWith("server jar", version.getServerJars(), ".jar");
        endsWith("server exe", version.<String[]>getValueOf(ServerExe.class), ".exe");
        endsWith("client mapping", version.getClientMappings(), ".txt");
        endsWith("server mapping", version.getServerMappings(), ".txt");
        endsWith("caves and cliffs data pack", version.<String>getValueOf(CavesAndCliffsDataPack.class), ".zip");
        endsWith("server zip", version.<String[]>getValueOf(ServerZip.class), ".zip");
    }

    private void endsWith(@NotNull String what, @Nullable String[] array, @NotNull String suffix) {
        if (array != null) {
            for (String s : array) {
                if (s == null) continue;
                assert version.ordinal() <= MCVersion.SNAPSHOT_13W41A.ordinal() ? s.endsWith(suffix) : s.contains(suffix) : "incorrect " + what + " @ " + version.name();
            }
        }
    }

    private boolean contains(@Nullable String[] array, @NotNull String s) {
        if (array == null) return false;
        for (String s1 : array) {
            if (Objects.equals(s1, s)) return true;
        }
        return false;
    }

    private void endsWith(@NotNull String what, @Nullable String s, @NotNull String suffix) {
        assert s == null || s.endsWith(suffix) : "incorrect " + what + " @ " + version.name();
    }

    public static String summarize(MCVersion version) {
        return "MCVersion{"
                + "protocolVersion=" + version.getProtocolVersion()
                + ", name='" + version.getName() + '\''
                + ", ordinal=" + version.ordinal()
                + ", name()='" + version.name() + '\''
                + '}';
    }
}
