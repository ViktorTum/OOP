package ru.nsu.tumilevich;

import java.util.ArrayList;
import java.util.List;


public class StudentGradeBook {
    /**
     * Представляет оценку по дисциплине
     */
    private static class Grade {
        private final int value;       // Оценка (2-5)
        private final boolean isExam;  // Является ли экзаменом
        private final int semester;    // Номер семестра

        public Grade(int value, boolean isExam, int semester) {
            this.value = value;
            this.isExam = isExam;
            this.semester = semester;
        }

        public int getValue() { return value; }
        public boolean isExam() { return isExam; }
        public int getSemester() { return semester; }
    }

    private final List<Grade> grades = new ArrayList<>();
    private int qualificationWorkGrade = 0; // 0 - не защищена
    private int currentSemester;

    public StudentGradeBook(int currentSemester) {
        this.currentSemester = currentSemester;
    }

    public void addGrade(int value, boolean isExam, int semester) {
        grades.add(new Grade(value, isExam, semester));
    }

    public void setQualificationWorkGrade(int grade) {
        this.qualificationWorkGrade = grade;
    }

    public double getCurrentAverageGrade() {
        if (grades.isEmpty() && qualificationWorkGrade == 0) {
            return 0.0;
        }

        int totalSum = 0;
        int count = grades.size();

        if (qualificationWorkGrade != 0) {
            totalSum += qualificationWorkGrade;
            count++;
        }

        for (Grade grade : grades) {
            totalSum += grade.getValue();
        }

        return (double) totalSum / count;
    }

    public boolean canTransferToBudget() {
        int lastSemester = currentSemester;
        int secondLastSemester = currentSemester - 1;

        for (Grade grade : grades) {
            int sem = grade.getSemester();
            if ((sem == lastSemester || sem == secondLastSemester) && grade.isExam()) {
                if (grade.getValue() == 3) {
                    return false;
                }
            }
        }
        return true;
    }


    public boolean canGetRedDiploma() {
        if (qualificationWorkGrade != 5) {
            return false;
        }

        int totalGrades = grades.size() + 1;
        int excellentCount = 0;
        boolean hasSatisfactory = false;

        for (Grade grade : grades) {
            if (grade.getValue() == 3) {
                hasSatisfactory = true;
            }
            if (grade.getValue() == 5) {
                excellentCount++;
            }
        }
        if (qualificationWorkGrade == 5) {
            excellentCount++;
        }

        return !hasSatisfactory && (double) excellentCount / totalGrades >= 0.75;
    }

    public boolean canGetHigherScholarship() {
        for (Grade grade : grades) {
            if (grade.getSemester() == currentSemester && grade.isExam()) {
                if (grade.getValue() == 3) {
                    return false;
                }
            }
        }
        return true;
    }

    public void setCurrentSemester(int semester) {
        this.currentSemester = semester;
    }
}