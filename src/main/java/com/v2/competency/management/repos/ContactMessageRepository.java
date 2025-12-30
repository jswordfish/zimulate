package com.v2.competency.management.repos;

import org.springframework.data.jpa.repository.JpaRepository;

import com.v2.competency.management.entities.ContactMessage;

public interface ContactMessageRepository extends JpaRepository<ContactMessage, Long> {

}
