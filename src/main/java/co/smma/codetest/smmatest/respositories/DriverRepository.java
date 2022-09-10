package co.smma.codetest.smmatest.respositories;

import co.smma.codetest.smmatest.entities.Driver;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface DriverRepository extends JpaRepository<Driver, Long> {

    Driver getDriverById(Long id);

    @Modifying(clearAutomatically=true)
    @Query(value = "UPDATE driver SET status = 'Active' WHERE id = :id ", nativeQuery = true)
    void setDriverToActive(@Param("id") Long id);

    @Modifying(clearAutomatically=true)
    @Query(value = "UPDATE driver SET status = 'Off' WHERE id = :id ", nativeQuery = true)
    void setDriverToInctive(@Param("id") Long id);

    @Modifying(clearAutomatically=true)
    @Query(value = "UPDATE driver SET status = 'Active' WHERE id = :id and status ='OnJob' ", nativeQuery = true)
    void deassignDriver(@Param("id") Long id);

    @Query(value = "SELECT * from driver", nativeQuery = true)
    List<Driver> findAll();


}
