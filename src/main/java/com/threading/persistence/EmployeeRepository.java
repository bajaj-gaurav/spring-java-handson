package com.threading.persistence;

import org.springframework.data.repository.CrudRepository;


public interface EmployeeRepository extends CrudRepository<Employee,Long> {
}