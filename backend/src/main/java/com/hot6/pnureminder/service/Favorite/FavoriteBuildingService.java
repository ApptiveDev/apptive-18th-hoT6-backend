package com.hot6.pnureminder.service.Favorite;

import com.hot6.pnureminder.dto.Favorite.FavoriteBuildingRoomListDto;
import com.hot6.pnureminder.dto.Favorite.FavoriteDepartmentAnnouncementDto;
import com.hot6.pnureminder.dto.LectureRoomDto;
import com.hot6.pnureminder.entity.Building;
import com.hot6.pnureminder.entity.Department;
import com.hot6.pnureminder.entity.Favorites.FavoriteBuilding;
import com.hot6.pnureminder.entity.Favorites.FavoriteDepartment;
import com.hot6.pnureminder.entity.Member;
import com.hot6.pnureminder.repository.Favorites.FavoriteBuildingRepository;
import com.hot6.pnureminder.service.BuildingService;
import com.hot6.pnureminder.service.DepartmentService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FavoriteBuildingService {

    private final FavoriteBuildingRepository favoriteBuildingRepository;
    private final DepartmentService departmentService;
    private final BuildingService buildingService;

    public List<FavoriteBuilding> getFavorites(Member member) {
        return favoriteBuildingRepository.findByMember(member);
    }

    public List<FavoriteBuildingRoomListDto> getFavoriteBuildings(Member member) {
        List<FavoriteBuilding> favoriteBuildings = favoriteBuildingRepository.findByMember(member);

        List<FavoriteBuildingRoomListDto> favoriteBuildingRoomListDtos = new ArrayList<>();

        for (FavoriteBuilding favoriteBuilding : favoriteBuildings) {
            String buildingName = favoriteBuilding.getBuilding().getBuildingName();
            List<LectureRoomDto> lectureRooms =
                    buildingService.findRoomsByBuildingNameAndSetTimeNow(buildingName);
            FavoriteBuildingRoomListDto dto =
                    new FavoriteBuildingRoomListDto(buildingName, lectureRooms);
            favoriteBuildingRoomListDtos.add(dto);
        }
        return favoriteBuildingRoomListDtos;
    }

    public FavoriteBuilding saveFavorite(FavoriteBuilding favorite) {
        return favoriteBuildingRepository.save(favorite);
    }

    public void toggleFavorite(Member member, String BuildingName) {

        Building building = buildingService.findByBuildingName(BuildingName);
        Optional<FavoriteBuilding> favoriteBuildingOptional = favoriteBuildingRepository.findByMemberAndBuilding(member, building);

        if (favoriteBuildingOptional.isPresent()) {
            // 이미 즐겨찾기에 추가되어 있다면 삭제
            favoriteBuildingRepository.delete(favoriteBuildingOptional.get());
        } else {
            // 즐겨찾기에 없다면 추가
            FavoriteBuilding favoriteBuilding = new FavoriteBuilding();
            favoriteBuilding.setMember(member);
            favoriteBuilding.setBuilding(building);
            this.saveFavorite(favoriteBuilding);
        }
    }
}
