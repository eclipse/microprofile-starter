package [# th:text="${java_package}"/].graphql.model;

import org.eclipse.microprofile.graphql.Description;
import org.eclipse.microprofile.graphql.NonNull;

import java.util.List;

public class SuperHero {
    private List<Team> teamAffiliations;
    private List<@NonNull String> superPowers;
    private String primaryLocation;
    @Description("Super hero name/nickname")
    private String name;
    private String realName;

    public SuperHero() {
    }

    public SuperHero(List<Team> teamAffiliations,
                     List<String> superPowers,
                     String primaryLocation,
                     String name,
                     String realName) {

        this.teamAffiliations = teamAffiliations;
        this.superPowers = superPowers;
        this.primaryLocation = primaryLocation;
        this.name = name;
        this.realName = realName;
    }

    public List<Team> getTeamAffiliations() {
        return teamAffiliations;
    }

    public List<String> getSuperPowers() {
        return superPowers;
    }

    @Description("Location where you are most likely to find this hero")
    public String getPrimaryLocation() {
        return primaryLocation;
    }


    public String getName() {
        return name;
    }

    public String getRealName() {
        return realName;
    }

    @NonNull
    public void setName(String name) {
        this.name = name;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    @Override
    public String toString() {
        return "SuperHero{"
                + ", superPowers=" + superPowers
                + ", primaryLocation=" + primaryLocation
                + ", name=" + name
                + ", realName=" + realName + '}';
    }
}
