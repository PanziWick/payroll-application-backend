package com.mexxar.payroll.address;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

interface AddressRepository extends JpaRepository<AddressModel, Long> {
    Page<AddressModel> findAll(Pageable pageable);
}
