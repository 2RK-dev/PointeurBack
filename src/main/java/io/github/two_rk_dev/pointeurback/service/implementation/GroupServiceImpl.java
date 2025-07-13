package io.github.two_rk_dev.pointeurback.service.implementation;

import io.github.two_rk_dev.pointeurback.dto.GroupDTO;
import io.github.two_rk_dev.pointeurback.dto.UpdateGroupDTO;
import io.github.two_rk_dev.pointeurback.exception.GroupNotFoundException;
import io.github.two_rk_dev.pointeurback.mapper.GroupMapper;
import io.github.two_rk_dev.pointeurback.model.Group;
import io.github.two_rk_dev.pointeurback.repository.GroupRepository;
import io.github.two_rk_dev.pointeurback.service.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GroupServiceImpl implements GroupService {

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private GroupMapper groupMapper;

    @Override
   public GroupDTO getGroupByLevel(Long levelId, Long groupId){
        Group existing = groupRepository.findByLevelIdAndId(levelId,groupId);
        if (existing == null) {
            throw new GroupNotFoundException("Groupe non Existant");
        }
        GroupDTO newgroup = groupMapper.toDto(existing);
        return newgroup;

    };

    @Override
    public GroupDTO updateGroup(Long levelId, Long groupId, UpdateGroupDTO dto) {
        // 1. Recherche de l'entité existante
        Group existing = groupRepository.findByLevelIdAndId(levelId, groupId);
        if (existing == null) {
            throw new GroupNotFoundException("Groupe non trouvé pour levelId: " + levelId + " et groupId: " + groupId);
        }
        // 2. Mise à jour de l'entité
        groupMapper.updateFromUpdateDto(dto, existing);

        // 3. Sauvegarde et retour du DTO
        Group updated = groupRepository.save(existing);
        return groupMapper.toDto(updated);
    };

    @Override
    public void deleteGroup(Long levelId, Long groupId) {
        // 1. Recherche et validation de l'existence du groupe
        Group existing = groupRepository.findByLevelIdAndId(levelId, groupId);
        if (existing == null) {
            new GroupNotFoundException(String.format("Groupe non trouvé - LevelId: %d, GroupId: %d", levelId, groupId));
        }

        // 2. Vérification des contraintes métier avant suppression
        if (!existing.getSchedules().isEmpty()) {
            throw new IllegalStateException("Impossible de supprimer un groupe avec des schedules associés");
        }

        // 3. Suppression effective
        groupRepository.delete(existing);
    }

}
