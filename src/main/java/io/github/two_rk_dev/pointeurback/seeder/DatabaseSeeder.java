package io.github.two_rk_dev.pointeurback.seeder;

import io.github.two_rk_dev.pointeurback.model.*;
import io.github.two_rk_dev.pointeurback.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Profile("seed")
@RequiredArgsConstructor
public class DatabaseSeeder implements CommandLineRunner {
    private final GroupRepository groupRepository;
    private final LevelRepository levelRepository;
    private final RoomRepository roomRepository;
    private final TeacherRepository teacherRepository;
    private final TeachingUnitRepository teachingUnitRepository;

    @Override
    public void run(String... args) {
        Level l1 = new Level();
        l1.setName("L1");
        l1.setAbbreviation("L1");
        Level l2 = new Level();
        l2.setName("L2");
        l2.setAbbreviation("L2");
        Level l3 = new Level();
        l3.setName("L3");
        l3.setAbbreviation("L3");
        levelRepository.saveAll(List.of(l1, l2, l3));

        Group l1Group1 = new Group();
        l1Group1.setName("L1Gp1");
        l1Group1.setSize(100);
        l1Group1.setLevel(l1);
        Group l1Group2 = new Group();
        l1Group2.setName("L1Gp2");
        l1Group2.setSize(50);
        l1Group2.setLevel(l1);
        Group l2Group1 = new Group();
        l2Group1.setName("L2Gp1");
        l2Group1.setSize(150);
        l2Group1.setLevel(l2);
        Group l2Group2 = new Group();
        l2Group2.setName("L2Gp2");
        l2Group2.setSize(200);
        l2Group2.setLevel(l2);
        groupRepository.saveAll(List.of(l1Group1, l1Group2, l2Group1, l2Group2));

        Room s001 = new Room();
        s001.setName("S001");
        s001.setAbbreviation("S001");
        s001.setSize(100);
        Room s002 = new Room();
        s002.setName("S002");
        s002.setAbbreviation("S002");
        s002.setSize(200);
        Room s003 = new Room();
        s003.setName("S003");
        s003.setAbbreviation("S003");
        s003.setSize(150);
        Room s101 = new Room();
        s101.setName("S101");
        s101.setAbbreviation("S101");
        s101.setSize(250);
        roomRepository.saveAll(List.of(s001, s002, s003, s101));

        Teacher teacher1 = new Teacher();
        teacher1.setName("Alix");
        teacher1.setAbbreviation("ALX");
        Teacher teacher2 = new Teacher();
        teacher2.setName("Andry");
        teacher2.setAbbreviation("ANDR");
        Teacher teacher3 = new Teacher();
        teacher3.setName("Angelo");
        teacher3.setAbbreviation("ANG");
        Teacher teacher4 = new Teacher();
        teacher4.setName("Bertin");
        teacher4.setAbbreviation("BERT");
        Teacher teacher5 = new Teacher();
        teacher5.setName("Cyprien");
        teacher5.setAbbreviation("CYPR");
        Teacher teacher6 = new Teacher();
        teacher6.setName("Jean Christian RALAIVAO");
        teacher6.setAbbreviation("JCR");
        teacherRepository.saveAll(List.of(teacher1, teacher2, teacher3, teacher4, teacher5, teacher6, teacher6));

        TeachingUnit tu1 = new TeachingUnit();
        tu1.setName("Mathématiques Appliquées");
        tu1.setAbbreviation("MATH");
        tu1.setLevel(l1);
        TeachingUnit tu2 = new TeachingUnit();
        tu2.setName("Programmation Web");
        tu2.setAbbreviation("PROG");
        tu2.setLevel(l2);
        TeachingUnit tu3 = new TeachingUnit();
        tu3.setName("Base de Données");
        tu3.setAbbreviation("BDD");
        tu3.setLevel(l2);
        TeachingUnit tu4 = new TeachingUnit();
        tu4.setName("Théorie des Réseaux");
        tu4.setAbbreviation("TRES");
        tu4.setLevel(l1);
        TeachingUnit tu5 = new TeachingUnit();
        tu5.setName("Langage C");
        tu5.setAbbreviation("LANG");
        tu5.setLevel(l1);
        teachingUnitRepository.saveAll(List.of(tu1, tu2, tu3, tu4, tu5, tu5));
    }
}
