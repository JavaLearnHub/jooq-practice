package org.konstde00.jooqpractice.repositories;

import lombok.AllArgsConstructor;

import org.jooq.Configuration;
import org.jooq.DSLContext;

import org.jooq.TransactionalCallable;
import org.jooq.impl.DSL;
import org.konstde00.jooqpractice.sql.schema.jooq.enums.EmployeesGender;
import org.konstde00.jooqpractice.sql.schema.jooq.tables.records.EmployeesRecord;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.stereotype.Repository;


import java.time.LocalDate;

import static org.konstde00.jooqpractice.sql.schema.jooq.Tables.*;

@AllArgsConstructor
@Repository
public class TransactionRepository implements SmartInitializingSingleton {

    private final DSLContext dslContext;

    public EmployeesRecord setNewEmployeeAndHisDepartment(int empNo, LocalDate birthDate, String firstName,
                                                          String lastName, EmployeesGender gender, LocalDate hireDate, String deptNo,
                                                          LocalDate fromDate, LocalDate toDate) {

        EmployeesRecord result = dslContext.transactionResult(configuration -> {

                DSLContext transactionConfiguration = DSL.using(configuration);

                EmployeesRecord newEmployee = transactionConfiguration
                        .insertInto(EMPLOYEES, EMPLOYEES.EMP_NO, EMPLOYEES.BIRTH_DATE, EMPLOYEES.FIRST_NAME,
                                EMPLOYEES.LAST_NAME, EMPLOYEES.GENDER, EMPLOYEES.HIRE_DATE)
                        .values(empNo, birthDate, firstName, lastName, gender, hireDate)
                        .returning(EMPLOYEES.EMP_NO, EMPLOYEES.BIRTH_DATE, EMPLOYEES.FIRST_NAME,
                                EMPLOYEES.LAST_NAME, EMPLOYEES.GENDER, EMPLOYEES.HIRE_DATE)
                        .fetchOne();

                transactionConfiguration
                        .insertInto(DEPT_EMP, DEPT_EMP.EMP_NO, DEPT_EMP.DEPT_NO, DEPT_EMP.FROM_DATE,
                                DEPT_EMP.TO_DATE)
                        .values(empNo, deptNo, fromDate, toDate)
                        .execute();
                return newEmployee;
        });

//        System.out.println("Resultttttt: " + result.getBirthDate());
        return result;
    }

    @Override
    public void afterSingletonsInstantiated() {
        EmployeesRecord employee = setNewEmployeeAndHisDepartment(
                500006,
                LocalDate.of(2000, 5, 12),
                "Timur",
                "Babayan",
                EmployeesGender.M,
                LocalDate.of(2024, 9, 10),
                "d005",
                LocalDate.of(2024, 9, 15),
                LocalDate.of(2035, 9, 15));
        System.out.println("Result: " + employee.getEmpNo());
    }

}
