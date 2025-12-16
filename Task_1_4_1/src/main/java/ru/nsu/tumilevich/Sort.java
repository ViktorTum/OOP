package ru.nsu.tumilevich;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Перечисление для всех возможных оценок.
 */
enum Grade {
    ОТЛИЧНО(5),
    ХОРОШО(4),
    УДОВЛЕТВОРИТЕЛЬНО(3),
    НЕУДОВЛЕТВОРИТЕЛЬНО(2),
    ЗАЧТЕНО(5), // Условно для расчета среднего балла
    НЕЗАЧТЕНО(2);

    private final int numericValue;

    Grade(int value) {
        this.numericValue = value;
    }

    public int getNumericValue() {
        return numericValue;
    }
}

/**
 * Перечисление для типов контроля.
 */
enum ControlType {
    ЭКЗАМЕН,
    ДИФФЕРЕНЦИРОВАННЫЙ_ЗАЧЕТ,
    ЗАЧЕТ,
    КУРСОВАЯ_РАБОТА,
    ЗАЩИТА_ВКР // Квалификационная работа
}

/**
 * Класс для представления курса или дисциплины.
 */
class Course {
    private String name;
    private int semester;
    private ControlType controlType;
    private Grade grade;

    public Course(String name, int semester, ControlType controlType) {
        this.name = name;
        this.semester = semester;
        this.controlType = controlType;
    }

    // Геттеры и сеттеры
    public Grade getGrade() { return grade; }
    public void setGrade(Grade grade) { this.grade = grade; }
    public int getSemester() { return semester; }
    public ControlType getControlType() { return controlType; }
    public boolean isGraded() { return grade != null; } // Есть ли оценка
    public boolean isDiplomaCourse() {
        // Оценки, идущие в диплом (обычно экзамены и дифф. зачеты)
        return controlType == ControlType.ЭКЗАМЕН || controlType == ControlType.ДИФФЕРЕНЦИРОВАННЫЙ_ЗАЧЕТ || controlType == ControlType.ЗАЩИТА_ВКР;
    }
}

/**
 * Основной класс для электронной зачетной книжки студента.
 */
class Student {
    private String studentName;
    private boolean isBudgetStudent; // true - бюджет, false - платное
    private List<Course> courses = new ArrayList<>();
    private int currentSemester;

    public Student(String studentName, boolean isBudgetStudent, int currentSemester) {
        this.studentName = studentName;
        this.isBudgetStudent = isBudgetStudent;
        this.currentSemester = currentSemester;
    }

    public void addCourse(Course course) {
        this.courses.add(course);
    }

    /**
     * 1. Расчет текущего среднего балла за все время обучения.
     */
    public double calculateAverageGrade() {
        List<Course> gradedCourses = courses.stream()
                .filter(c -> c.isGraded() && (c.getControlType() == ControlType.ЭКЗАМЕН || c.getControlType() == ControlType.ДИФФЕРЕНЦИРОВАННЫЙ_ЗАЧЕТ))
                .collect(Collectors.toList());

        if (gradedCourses.isEmpty()) {
            return 0.0;
        }

        double sum = gradedCourses.stream()
                .mapToDouble(c -> c.getGrade().getNumericValue())
                .sum();

        return sum / gradedCourses.size();
    }

    /**
     * 2. Проверка возможности перевода на бюджет.
     * Условие: отсутствие оценок "удовлетворительно" за последние две сессии.
     */
    public boolean canTransferToBudget() {
        if (isBudgetStudent) {
            return false; // Уже на бюджете
        }

        // Определяем номера двух последних семестров
        int lastSemester = currentSemester - 1;
        int preLastSemester = currentSemester - 2;

        if (lastSemester < 1) return false;

        // Проверяем оценки за эти семестры
        long satisfactoryGrades = courses.stream()
                .filter(c -> c.isGraded() && (c.getSemester() == lastSemester || c.getSemester() == preLastSemester))
                .filter(c -> c.getControlType() == ControlType.ЭКЗАМЕН) // Только экзамены
                .filter(c -> c.getGrade() == Grade.УДОВЛЕТВОРИТЕЛЬНО)
                .count();

        return satisfactoryGrades == 0;
    }

    /**
     * 3. Проверка возможности получения "красного" диплома.
     */
    public boolean canGetRedDiploma() {
        List<Course> diplomaCourses = courses.stream()
                .filter(Course::isDiplomaCourse)
                .collect(Collectors.toList());

        // Проверка на отсутствие удовлетворительных оценок
        boolean hasSatisfactory = diplomaCourses.stream()
                .anyMatch(c -> c.isGraded() && c.getGrade() == Grade.УДОВЛЕТВОРИТЕЛЬНО);
        if (hasSatisfactory) {
            return false;
        }

        // Проверка, что ВКР на "отлично"
        boolean wkrExcellent = courses.stream()
                .filter(c -> c.getControlType() == ControlType.ЗАЩИТА_ВКР)
                .allMatch(c -> c.isGraded() && c.getGrade() == Grade.ОТЛИЧНО);
        if (!wkrExcellent) {
            return false;
        }

        // Проверка, что 75% оценок - "отлично"
        long excellentCount = diplomaCourses.stream()
                .filter(c -> c.isGraded() && c.getGrade() == Grade.ОТЛИЧНО)
                .count();

        return (double) excellentCount / diplomaCourses.size() >= 0.75;
    }

    /**
     * 4. Проверка возможности получения повышенной стипендии.
     * Обычно условие - сессия закрыта на "хорошо" и "отлично".
     */
    public boolean canGetIncreasedScholarship() {
        int lastSemester = currentSemester - 1;
        if (lastSemester < 1) return false;

        List<Course> lastSessionCourses = courses.stream()
                .filter(c -> c.getSemester() == lastSemester && c.isGraded())
                .collect(Collectors.toList());

        if (lastSessionCourses.isEmpty()) return false; // Сессия еще не закрыта

        // Проверка на отсутствие удовлетворительных оценок и пересдач
        boolean hasBadGrades = lastSessionCourses.stream()
                .anyMatch(c -> c.getGrade() == Grade.УДОВЛЕТВОРИТЕЛЬНО || c.getGrade() == Grade.НЕУДОВЛЕТВОРИТЕЛЬНО || c.getGrade() == Grade.НЕЗАЧТЕНО);

        return !hasBadGrades;
    }

    public static void main(String[] args) {
        // --- Пример использования ---
        Student student = new Student("Иванов И.И.", false, 3); // Студент-платник, перешел на 3-й семестр

        // Добавляем курсы за 1-й семестр
        Course math1 = new Course("Математический анализ", 1, ControlType.ЭКЗАМЕН);
        math1.setGrade(Grade.ОТЛИЧНО);
        student.addCourse(math1);

        Course history = new Course("История", 1, ControlType.ЗАЧЕТ);
        history.setGrade(Grade.ЗАЧТЕНО);
        student.addCourse(history);

        Course prog1 = new Course("Программирование", 1, ControlType.ДИФФЕРЕНЦИРОВАННЫЙ_ЗАЧЕТ);
        prog1.setGrade(Grade.ХОРОШО);
        student.addCourse(prog1);

        // Добавляем курсы за 2-й семестр
        Course math2 = new Course("Дискретная математика", 2, ControlType.ЭКЗАМЕН);
        math2.setGrade(Grade.ОТЛИЧНО);
        student.addCourse(math2);

        Course prog2 = new Course("Алгоритмы и структуры данных", 2, ControlType.ЭКЗАМЕН);
        prog2.setGrade(Grade.ХОРОШО);
        student.addCourse(prog2);

        // --- Вывод результатов ---
        System.out.println("Студент: " + student.studentName);
        System.out.printf("Средний балл: %.2f\n", student.calculateAverageGrade());
        System.out.println("Может ли перейти на бюджет: " + student.canTransferToBudget());
        System.out.println("Может ли получить красный диплом: " + student.canGetRedDiploma());
        System.out.println("Может ли получать повышенную стипендию: " + student.canGetIncreasedScholarship());
    }
}
