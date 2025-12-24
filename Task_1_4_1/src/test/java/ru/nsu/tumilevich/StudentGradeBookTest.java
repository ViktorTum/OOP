package ru.nsu.tumilevich;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class StudentGradeBookTest {

	@Test
	void testGetCurrentAverageGrade_NoGrades() {
		StudentGradeBook book = new StudentGradeBook(1);
		assertEquals(0.0, book.getCurrentAverageGrade(), 0.001);
	}

	@Test
	void testGetCurrentAverageGrade_WithGrades() {
		StudentGradeBook book = new StudentGradeBook(3);
		book.addGrade(5, true, 1);
		book.addGrade(4, true, 2);
		book.addGrade(3, false, 3);
		assertEquals(4.0, book.getCurrentAverageGrade(), 0.001); // (5+4+3)/3 = 4.0
	}

	@Test
	void testGetCurrentAverageGrade_WithQualificationWork() {
		StudentGradeBook book = new StudentGradeBook(8);
		book.addGrade(5, true, 1);
		book.addGrade(5, true, 2);
		book.setQualificationWorkGrade(5);
		assertEquals(5.0, book.getCurrentAverageGrade(), 0.001);
	}

	@Test
	void testCanTransferToBudget_SuccessfulTransfer() {
		StudentGradeBook book = new StudentGradeBook(8);
		book.addGrade(4, true, 7);
		book.addGrade(5, true, 7);
		book.addGrade(4, true, 8);
		assertTrue(book.canTransferToBudget());
	}

	@Test
	void testCanTransferToBudget_FailedDueToThree() {
		StudentGradeBook book = new StudentGradeBook(8);
		book.addGrade(3, true, 8);
		book.addGrade(5, true, 7);
		assertFalse(book.canTransferToBudget());
	}

	@Test
	void testCanTransferToBudget_IgnoreOldSemesters() {
		StudentGradeBook book = new StudentGradeBook(8);
		book.addGrade(3, true, 6);
		book.addGrade(4, true, 7);
		book.addGrade(5, true, 8);
		assertTrue(book.canTransferToBudget());
	}

	@Test
	void testCanTransferToBudget_IgnoreCredits() {
		StudentGradeBook book = new StudentGradeBook(8);
		book.addGrade(3, false, 8);
		book.addGrade(5, true, 8);
		assertTrue(book.canTransferToBudget());
	}

	@Test
	void testCanGetRedDiploma_QualificationNotFive() {
		StudentGradeBook book = new StudentGradeBook(8);
		book.setQualificationWorkGrade(4);
		assertFalse(book.canGetRedDiploma());
	}

	@Test
	void testCanGetRedDiploma_HasThree() {
		StudentGradeBook book = new StudentGradeBook(8);
		book.addGrade(3, true, 1);
		book.setQualificationWorkGrade(5);
		assertFalse(book.canGetRedDiploma());
	}

	@Test
	void testCanGetRedDiploma_LessThan75Percent() {
		StudentGradeBook book = new StudentGradeBook(8);
		book.addGrade(5, true, 1);
		book.addGrade(5, true, 2);
		book.setQualificationWorkGrade(5);
		assertTrue(book.canGetRedDiploma());

		book.addGrade(4, true, 3);
		book.addGrade(4, true, 4);
		assertFalse(book.canGetRedDiploma());
	}

	@Test
	void testCanGetRedDiploma_AllConditionsMet() {
		StudentGradeBook book = new StudentGradeBook(8);
		for (int i = 1; i <= 7; i++) {
			book.addGrade(5, true, i);
		}
		book.setQualificationWorkGrade(5);
		assertTrue(book.canGetRedDiploma());
	}

	@Test
	void testCanGetHigherScholarship_CurrentSemesterSuccess() {
		StudentGradeBook book = new StudentGradeBook(5);
		book.addGrade(5, true, 5);
		book.addGrade(4, true, 5);
		assertTrue(book.canGetHigherScholarship());
	}

	@Test
	void testCanGetHigherScholarship_HasThreeInCurrent() {
		StudentGradeBook book = new StudentGradeBook(5);
		book.addGrade(3, true, 5);
		assertFalse(book.canGetHigherScholarship());
	}

	@Test
	void testCanGetHigherScholarship_IgnoreOldSemesters() {
		StudentGradeBook book = new StudentGradeBook(5);
		book.addGrade(3, true, 4);
		book.addGrade(5, true, 5);
		assertTrue(book.canGetHigherScholarship());
	}

	@Test
	void testCanGetHigherScholarship_IgnoreCredits() {
		StudentGradeBook book = new StudentGradeBook(5);
		book.addGrade(3, false, 5);
		book.addGrade(5, true, 5);
		assertTrue(book.canGetHigherScholarship());
	}

	@Test
	void testEdgeCases_CurrentSemesterChange() {
		StudentGradeBook book = new StudentGradeBook(2);
		book.addGrade(3, true, 1);
		book.addGrade(5, true, 2);
		assertFalse(book.canTransferToBudget());

		book.setCurrentSemester(3);
		assertTrue(book.canTransferToBudget());
	}
}