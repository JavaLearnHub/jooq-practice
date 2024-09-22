package org.konstde00.jooqpractice.repositories;

import lombok.AllArgsConstructor;
import lombok.val;
import org.jooq.DSLContext;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

import static org.jooq.impl.DSL.*;
import static org.konstde00.jooqpractice.sql.schema.jooq.Tables.*;

@Repository
@AllArgsConstructor
public class EmployeesRepository implements SmartInitializingSingleton {

    private final DSLContext dslContext;

    public List<String> getDepartmentsWithSeniorEngineers() {
        return dslContext.select(DEPARTMENTS.DEPT_NAME,
                        count(DEPT_EMP.EMP_NO))
                .from(DEPT_EMP
                        .join(DEPARTMENTS)
                        .on(DEPT_EMP.DEPT_NO.eq(DEPARTMENTS.DEPT_NO))
                        .join(TITLES).on(DEPT_EMP.EMP_NO.eq(TITLES.EMP_NO)))
                .where(TITLES.TITLE.eq(inline("Senior Engineer")))
                .groupBy(DEPARTMENTS.DEPT_NAME)
                .having(count(DEPT_EMP.EMP_NO)
                        .greaterThan(5))
                .fetch(record -> record.get(DEPARTMENTS.DEPT_NAME));
    }

    public List<Integer> getEmployeesWithMoreThanOneTitle() {
        return dslContext.select(TITLES.EMP_NO,
                        count(TITLES.EMP_NO))
                .from(TITLES)
                .groupBy(TITLES.EMP_NO)
                .having(count(TITLES.EMP_NO)
                        .greaterThan(1))
                .fetch(record -> record.get(TITLES.EMP_NO));
    }

    public List<String> getDepartmentsWithCertainMinimumSalaryAndCertainAverageSalaryAndMoreThanEightEmployees() {
        return dslContext.select(DEPARTMENTS.DEPT_NAME)
                .from(DEPARTMENTS
                        .join(DEPT_EMP)
                        .on(DEPT_EMP.DEPT_NO.eq(DEPARTMENTS.DEPT_NO))
                        .join(SALARIES)
                        .on(DEPT_EMP.EMP_NO.eq(SALARIES.EMP_NO)))
                .groupBy(DEPARTMENTS.DEPT_NAME)
                .having(min(SALARIES.SALARY)
                        .greaterThan(40000)
                        .and(avg(SALARIES.SALARY)
                                .greaterThan(BigDecimal.valueOf(65000))
                                .and(count(DEPT_EMP.EMP_NO).greaterThan(8))))
                .fetch(record -> record.get(DEPARTMENTS.DEPT_NAME));
    }

    public List<String> getDepartmentsWithMaximumSalaryMoreThanTwiceMinimum() {
        val max_and_min_dept_salary_cte =
                name("max_and_min_dept_salary_cte")
                        .fields("dept_name", "max_salary", "min_salary")
                        .as(select(DEPARTMENTS.DEPT_NAME,
                                max(SALARIES.SALARY),
                                min(SALARIES.SALARY))
                                .from(DEPARTMENTS
                                        .join(DEPT_EMP)
                                        .on(DEPARTMENTS.DEPT_NO.eq(DEPT_EMP.DEPT_NO))
                                        .join(SALARIES)
                                        .on(DEPT_EMP.EMP_NO.eq(SALARIES.EMP_NO)))
                                .groupBy(DEPARTMENTS.DEPT_NAME));

        return dslContext.with(max_and_min_dept_salary_cte)
                .select(max_and_min_dept_salary_cte
                        .field("dept_name"))
                .from(max_and_min_dept_salary_cte)
                .where(max_and_min_dept_salary_cte
                        .field("max_salary", Integer.class)
                        .greaterThan(max_and_min_dept_salary_cte
                                .field("min_salary", Integer.class)
                                .mul(2)))
                .fetch(record -> record
                .get(max_and_min_dept_salary_cte.field("dept_name", String.class)));
    }

    public List<Integer> getEmployeesWithMoreThanThreeDepartments() {
        return dslContext.select(DEPT_EMP.EMP_NO)
                .from(DEPT_EMP)
                .groupBy(DEPT_EMP.EMP_NO)
                .having(count(DEPT_EMP.EMP_NO).greaterThan(3))
                .fetch(record -> record.get(DEPT_EMP.EMP_NO));
    }

    public List<String> getTitlesWithAverageSalaryMoreThanCertainSalary() {
        return dslContext.select(TITLES.TITLE)
                .from(TITLES
                        .join(SALARIES)
                        .on(TITLES.EMP_NO.eq(SALARIES.EMP_NO)))
                .groupBy(TITLES.TITLE)
                .having(avg(SALARIES.SALARY).greaterThan(BigDecimal.valueOf(75000)))
                .fetch(record -> record.get(TITLES.TITLE));
    }

    public List<Integer> getYearsWithMoreThanFiftyHiredEmployees() {
        return dslContext.select(year(EMPLOYEES.HIRE_DATE))
                .from(EMPLOYEES)
                .groupBy(year(EMPLOYEES.HIRE_DATE))
                .having(count(EMPLOYEES.EMP_NO).greaterThan(50))
                .fetch(record -> record.get(year(EMPLOYEES.HIRE_DATE)));
    }

    public List<String> getDepartmentsWithMoreThanFifteenEmployeesTitles() {
        return dslContext.select(DEPARTMENTS.DEPT_NAME)
                .from(DEPARTMENTS
                        .join(DEPT_EMP)
                        .on(DEPT_EMP.DEPT_NO.eq(DEPARTMENTS.DEPT_NO))
                        .join(TITLES)
                        .on(DEPT_EMP.EMP_NO.eq(TITLES.EMP_NO)))
                .groupBy(DEPARTMENTS.DEPT_NAME)
                .having(count(TITLES.TITLE).greaterThan(15))
                .fetch(record -> record.get(DEPARTMENTS.DEPT_NAME));
    }

    public List<Integer> getEmployeesWithCertainDifferenceBetweenHighestAndLowestSalary() {
        return dslContext.select(SALARIES.EMP_NO)
                .from(SALARIES)
                .groupBy(SALARIES.EMP_NO)
                .having(max(SALARIES.SALARY)
                        .minus(min(SALARIES.SALARY))
                        .greaterThan(50000))
                .fetch(record -> record.get(SALARIES.EMP_NO));
    }

    public List<String> getDepartmentsWithMaxSalaryMoreThanCertainSalary() {
        return dslContext.select(DEPARTMENTS.DEPT_NAME)
                .from(DEPARTMENTS
                        .join(DEPT_EMP)
                        .on(DEPT_EMP.DEPT_NO.eq(DEPARTMENTS.DEPT_NO))
                        .join(SALARIES)
                        .on(DEPT_EMP.EMP_NO.eq(SALARIES.EMP_NO)))
                .groupBy(DEPARTMENTS.DEPT_NAME)
                .having(max(SALARIES.SALARY).greaterThan(200000))
                .fetch(record -> record.get(DEPARTMENTS.DEPT_NAME));
    }

    @Override
    public void afterSingletonsInstantiated() {
//        List<String> departments = getDepartmentsWithCertainMinimumSalaryAndCertainAverageSalaryAndMoreThanEightEmployees();
//        System.out.println("Result: " + departments);
    }
}

