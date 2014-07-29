/**
 * 
 */
package org.pshow.repository;

import java.util.List;

import org.pshow.domain.User;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

/**
 * @author topcat
 *
 */
public interface UserDao extends PagingAndSortingRepository<User, Long>, JpaSpecificationExecutor<User> {

	User findByName(String name);
	
	@Query("from User u where u.id in (:ids)")
	List<User> findByIds(@Param("ids") List<Long> ids);

	@Modifying
	@Query("delete from User u where u.id in (:ids)")
	void delete(@Param("ids") List<Long> ids);

	@Modifying
	@Query("update User u set u.locked = :locked where u.id in (:ids)")
	void updateLock(@Param("ids") List<Long> l_ids, @Param("locked") boolean isLocked);

}
