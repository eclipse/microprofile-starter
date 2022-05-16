package [# th:text="${java_package}"/].graphql;

import [# th:text="${java_package}"/].graphql.db.DuplicateSuperHeroException;
import [# th:text="${java_package}"/].graphql.db.HeroDatabase;
import [# th:text="${java_package}"/].graphql.db.UnknownHeroException;
import [# th:text="${java_package}"/].graphql.db.UnknownTeamException;
import [# th:text="${java_package}"/].graphql.model.SuperHero;
import [# th:text="${java_package}"/].graphql.model.Team;
import org.eclipse.microprofile.graphql.*;

import [# th:text="${jakarta_ee_package}"/].inject.Inject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@GraphQLApi
public class HeroFinder {
    private static final Logger LOG = Logger.getLogger(HeroFinder.class.getName());

    @Inject
    private HeroDatabase heroDB;

    @Query
    public SuperHero superHero(@Name("name") @Description("Super hero name, not real name") String name) throws UnknownHeroException {
        LOG.log(Level.INFO, "superHero invoked [{0}]", name);
        return Optional.ofNullable(heroDB.getHero(name)).orElseThrow(() -> new UnknownHeroException(name));
    }

    @Query
    @Description("List all super heroes in the database")
    public Collection<SuperHero> allHeroes() {
        LOG.info("allHeroes invoked");
        return heroDB.getAllHeroes();
    }

    @Query
    public Collection<SuperHero> allHeroesIn(@DefaultValue("New York, NY") @Name("city") String city) {
        LOG.log(Level.INFO, "allHeroesIn invoked [{0}]", city);
        return allHeroesByFilter(hero -> {
            return city.equals(hero.getPrimaryLocation());
        });
    }

    @Query
    public Collection<SuperHero> allHeroesWithPower(@Name("power") String power) {
        LOG.log(Level.INFO, "allHeroesWithPower invoked [{0}]", power);
        return allHeroesByFilter(hero -> {
            return hero.getSuperPowers().contains(power);
        });
    }

    @Query
    public Collection<SuperHero> allHeroesInTeam(@Name("team") String teamName) throws UnknownTeamException {
        LOG.log(Level.INFO, "allHeroesInTeam invoked [{0}]", teamName);
        return heroDB.getTeam(teamName).getMembers();
    }

    @Query
    public Team getTeam(@Name("team") String teamName) throws UnknownTeamException {
        LOG.log(Level.INFO, "getTeam invoked [{0}]", teamName);
        return heroDB.getTeam(teamName);
    }

    @Query
    public Collection<Team> allTeams() {
        LOG.info("allTeams invoked");
        return heroDB.getAllTeams();
    }

    @Mutation
    public SuperHero createNewHero(@Name("hero") SuperHero newHero) throws DuplicateSuperHeroException, UnknownHeroException {
        LOG.log(Level.INFO, "createNewHero invoked [{0}]", newHero);
        heroDB.addHero(newHero);
        return heroDB.getHero(newHero.getName());
    }

    @Mutation
    public Collection<SuperHero> createNewHeroes(@Name("heroes") List<SuperHero> newHeroes) throws DuplicateSuperHeroException, UnknownHeroException {
        LOG.log(Level.INFO, "createNewHeroes invoked [{0}]", newHeroes);
        heroDB.addHeroes(newHeroes);
        return newHeroes;
    }

    @Mutation
    @Description("Adds a hero to the specified team and returns the updated team.")
    public Team addHeroToTeam(@Name("hero") String heroName,
                              @Name("team") String teamName)
            throws UnknownTeamException, UnknownHeroException {

        LOG.log(Level.INFO, "addHeroToTeam invoked [{0}],[{1}]", new Object[]{heroName, teamName});
        return heroDB.getTeam(teamName)
                .addMembers(heroDB.getHero(heroName));
    }

    @Mutation
    @Description("Removes a hero to the specified team and returns the updated team.")
    public Team removeHeroFromTeam(@Name("hero") String heroName,
                                   @Name("team") String teamName)
            throws UnknownTeamException, UnknownHeroException {
        LOG.log(Level.INFO, "removeHeroFromTeam invoked [{0}],[{1}]", new Object[]{heroName, teamName});
        return heroDB.getTeam(teamName)
                .removeMembers(heroDB.getHero(heroName));
    }

    @Mutation
    @Description("Removes a hero... permanently...")
    public Collection<SuperHero> removeHero(@Name("hero") String heroName) throws UnknownHeroException {
        LOG.log(Level.INFO, "removeHero invoked [{0}]", heroName);
        if (heroDB.removeHero(heroName) == null) {
            throw new UnknownHeroException(heroName);
        }
        return allHeroes();
    }

    @Mutation("setRivalTeam")
    public Team setRivalTeam(@Name("teamName") String teamName, @Name("rivalTeam") Team rivalTeam)
            throws UnknownTeamException {

        LOG.log(Level.INFO, "setRivalTeam invoked [{0}],[{1}]", new Object[]{teamName, rivalTeam});
        Team team = heroDB.getTeam(teamName);
        team.setRivalTeam(rivalTeam);
        return team;
    }


    @Mutation
    public Team createNewTeam(@Name("newTeam") Team newTeam) {
        LOG.log(Level.INFO, "createNewTeam invoked [{0}]", newTeam);
        List<SuperHero> members = newTeam.getMembers();
        Team team = heroDB.createNewTeam(newTeam.getName());
        if (members != null && members.size() > 0) {
            team.setMembers(members);
        }
        team.setRivalTeam(newTeam.getRivalTeam());
        return team;
    }

    @Mutation
    public Team removeTeam(@Name("teamName") String teamName) throws UnknownTeamException {
        LOG.log(Level.INFO, "removeTeam invoked [{0}]", teamName);
        return heroDB.removeTeam(teamName);
    }

    private Collection<SuperHero> allHeroesByFilter(Predicate<SuperHero> predicate) {
        return heroDB.getAllHeroes()
                .stream()
                .filter(predicate)
                .collect(Collectors.toCollection(ArrayList::new));
    }
}
