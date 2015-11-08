package io.fourfinanceit.homework.jpa.repositorires;

import io.fourfinanceit.homework.jpa.domain.Attempt;

import java.util.Date;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AttemptRepository extends JpaRepository<Attempt, Long> {

	Long countByIpAndRequestDateAfter(String ip, Date date);
}
