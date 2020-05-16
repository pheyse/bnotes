package de.brightside.bnotes.dao;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import de.brightside.bnotes.model.Picture;
import de.brightside.bnotes.model.PictureIdNameAndType;
import de.brightside.bnotes.model.PictureInfo;

public interface PictureRepo extends JpaRepository<Picture,Long>{

	Picture findByChapterIdAndName(long chapterId, String useName);

	Collection<PictureIdNameAndType> findByChapterId(long chapterId);
	
    @Query(value = "SELECT picId FROM Picture")
	public List<Long> getAllIds();

    @Query(value = "SELECT new de.brightside.bnotes.model.PictureInfo(p.id, p.name, p.type) from Picture p")
    Collection<PictureInfo> getAllIdsNamesAndTypes();
}
