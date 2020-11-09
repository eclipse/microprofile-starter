package [# th:text="${java_package}"/].graphql.model;

import org.eclipse.microprofile.graphql.NonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Team {

    private String name;
    private List<SuperHero> members;
    private Team rivalTeam;

    public Team() {

    }

    public Team(String name, List<SuperHero> members) {
        this.name = name;
        this.members = members;
    }

    @NonNull
    public String getName() {
        return name;
    }

    public List<@NonNull SuperHero> getMembers() {
        return members;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setMembers(List<SuperHero> members) {
        this.members = members;
    }

    public Team addMembers(SuperHero... heroes) {
        if (members == null) {
            members = new ArrayList<>();
        }
        members.addAll(Arrays.asList(heroes));
        return this;
    }

    public Team removeMembers(SuperHero... heroes) {
        if (members != null) {
            for (SuperHero hero : heroes) {
                members.remove(hero);
            }
        }
        return this;
    }

    public Team getRivalTeam() {
        return rivalTeam;
    }

    public void setRivalTeam(Team rivalTeam) {
        this.rivalTeam = rivalTeam;
    }

    @Override
    public String toString() {
        return "Team{"
                + "name=" + name
                + ", members=" + members;
    }
}
