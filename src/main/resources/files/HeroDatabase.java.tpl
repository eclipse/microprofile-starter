package [# th:text="${java_package}"/].graphql.db;

import [# th:text="${java_package}"/].graphql.model.SuperHero;
import [# th:text="${java_package}"/].graphql.model.Team;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Initialized;
import javax.enterprise.event.Observes;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import java.util.*;

@ApplicationScoped
public class HeroDatabase {
    private final Map<String, SuperHero> allHeroes = new HashMap<>();
    private final Map<String, Team> allTeams = new HashMap<>();

    private void init(@Observes @Initialized(ApplicationScoped.class) Object init) {

        try {
            Jsonb jsonb = JsonbBuilder.create();
            String mapJson = getInitalJson();
            addHeroes(jsonb.fromJson(mapJson,
                    new ArrayList<SuperHero>() {
                    }.getClass().getGenericSuperclass()));
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public SuperHero getHero(String name) throws UnknownHeroException {
        SuperHero superHero = allHeroes.get(name);

        if (superHero == null) {
            throw new UnknownHeroException(name);
        }

        return superHero;
    }

    public Team getTeam(String name) throws UnknownTeamException {
        Team team = allTeams.get(name);
        if (team == null) {
            throw new UnknownTeamException(name);
        }
        return team;
    }

    public Collection<SuperHero> getAllHeroes() {
        return allHeroes.values();
    }

    public Collection<Team> getAllTeams() {
        return allTeams.values();
    }

    public int addHeroes(Collection<SuperHero> heroes) {
        int count = 0;
        for (SuperHero hero : heroes) {

            addHero(hero);
            count++;

        }
        return count;
    }

    public void addHero(SuperHero hero) {
        allHeroes.put(hero.getName(), hero);
        List<Team> teams = hero.getTeamAffiliations();
        if (teams != null) {
            ListIterator<Team> iter = teams.listIterator();
            while (iter.hasNext()) {
                Team team = iter.next();
                Team existingTeam = allTeams.get(team.getName());
                if (existingTeam == null) {
                    existingTeam = createNewTeam(team.getName());
                }
                iter.set(existingTeam);
                List<SuperHero> members = existingTeam.getMembers();
                if (members == null) {
                    members = new ArrayList<>();
                    existingTeam.setMembers(members);
                }
                members.add(hero);
            }
        }
    }

    public SuperHero removeHero(String heroName) {
        SuperHero hero = allHeroes.remove(heroName);
        if (hero == null) {
            return null;
        }
        for (Team team : getAllTeams()) {
            team.removeMembers(hero);
        }
        return hero;
    }

    public Team createNewTeam(String teamName, SuperHero... initialMembers) {
        Team newTeam = new Team();
        newTeam.setName(teamName);
        newTeam.addMembers(initialMembers);
        allTeams.put(teamName, newTeam);
        return newTeam;
    }

    public Team removeHeroesFromTeam(Team team, SuperHero... heroes) {
        team.removeMembers(heroes);
        for (SuperHero hero : heroes) {
            List<Team> teamAffiliations = hero.getTeamAffiliations();
            if (teamAffiliations != null) {
                teamAffiliations.remove(team);
            }
        }
        return team;
    }

    public Team removeHeroesFromTeam(Team team, Collection<SuperHero> heroes) {
        return removeHeroesFromTeam(team, heroes.toArray(new SuperHero[]{}));
    }

    public Team removeTeam(String teamName) throws UnknownTeamException {
        Team team = allTeams.remove(teamName);
        if (team == null) {
            throw new UnknownTeamException(teamName);
        }
        return removeHeroesFromTeam(team, allHeroes.values());
    }

    private static String getInitalJson() {
        return "[" +
                "{" +
                "\"name\":\"Iron Man\"," +
                "\"realName\":\"Tony Stark\"," +
                "\"primaryLocation\":\"Los Angeles, CA\"," +
                "\"superPowers\":[\"wealth\",\"engineering\"]," +
                "\"teamAffiliations\":[{\"name\":\"Avengers\"}]" +
                "}," +
                "{" +
                "\"name\":\"Spider Man\"," +
                "\"realName\":\"Peter Parker\"," +
                "\"primaryLocation\":\"New York, NY\"," +
                "\"superPowers\":[\"Spidey Sense\",\"Wall-Crawling\",\"Super Strength\",\"Web-shooting\"]," +
                "\"teamAffiliations\":[{\"name\":\"Avengers\"}]" +
                "}," +
                "{" +
                "\"name\":\"Starlord\"," +
                "\"realName\":\"Peter Quill\"," +
                "\"primaryLocation\":\"Outer Space\"," +
                "\"superPowers\":[\"Ingenuity\",\"Humor\",\"Dance moves\"]," +
                "\"teamAffiliations\":[{\"name\":\"Guardians of the Galaxy\"}]" +
                "}," +
                "{" +
                "\"name\":\"Wolverine\"," +
                "\"realName\":\"James Howlett, aka Logan\"," +
                "\"primaryLocation\":\"Unknown\"," +
                "\"superPowers\":[\"Regeneritive Healing\",\"Enhanced Reflexes\",\"Adamantium-infused skeleton\",\"Retractable claws\"]," +
                "\"teamAffiliations\":[{\"name\":\"Avengers\"},{\"name\":\"X-Men\"}]" +
                "}" +
                "]";
    }
}
