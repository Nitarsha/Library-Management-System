import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

class Person {
    protected String name;
    Person(String name) { this.name = name; }
    public String getName() { return name; }
}

class LibraryItem {
    protected String title;
    LibraryItem(String title) { this.title = title; }
    public String getDetails() { return title; }
}

class Book extends LibraryItem {
    private String author;
    private boolean issued;

    Book(String title, String author) {
        super(title);
        this.author = author;
    }

    public String getTitle() { return title; }
    public boolean isIssued() { return issued; }
    public void issueBook() { issued = true; }
    public void returnBook() { issued = false; }

    @Override
    public String getDetails() {
        return "📖 " + title + " by " + author;
    }
}

class Member extends Person {
    private int memberId;

    Member(String name, int memberId) {
        super(name);
        this.memberId = memberId;
    }

    public int getMemberId() { return memberId; }

    @Override
    public String toString() {
        return "👤 Member: " + name + " (ID: " + memberId + ")";
    }
}

class Transaction {
    private Book book;
    private Member member;
    private LocalDate issueDate, returnDate;

    Transaction(Book book, Member member) {
        this.book = book;
        this.member = member;
        issueDate = LocalDate.now();
        book.issueBook();
    }

    void returnBook(LocalDate date) {
        returnDate = date;
        book.returnBook();
    }

    int calculateFine() {
        if (returnDate == null) return 0;
        long late = ChronoUnit.DAYS.between(issueDate.plusDays(7), returnDate);
        return late > 0 ? (int) late * 2 : 0;
    }

    public Book getBook() { return book; }
    public Member getMember() { return member; }

    @Override
    public String toString() {
        String s = "📝 " + book.getTitle() + " issued to " + member.getName() + " on " + issueDate;
        if (returnDate != null)
            s += ", returned on " + returnDate + " | Fine: ₹" + calculateFine();
        return s;
    }
}

class Library {
    private List<Book> books = new ArrayList<>();
    private List<Member> members = new ArrayList<>();
    private List<Transaction> transactions = new ArrayList<>();

    void addBook(String t, String a) {
        books.add(new Book(t, a));
        System.out.println("✅ Book added successfully!");
    }

    void addMember(String n, int id) {
        members.add(new Member(n, id));
        System.out.println("✅ Member registered successfully!");
    }

    Book findBook(String title) {
        for (Book b : books)
            if (b.getTitle().equalsIgnoreCase(title)) return b;
        return null;
    }

    Member findMember(int id) {
        for (Member m : members)
            if (m.getMemberId() == id) return m;
        return null;
    }

    void issueBook(String title, int id) {
        Book b = findBook(title);
        Member m = findMember(id);

        if (b == null || m == null) {
            System.out.println("❌ Book or Member not found!");
            return;
        }

        if (b.isIssued()) {
            System.out.println("❌ Book already issued!");
            return;
        }

        transactions.add(new Transaction(b, m));
        System.out.println("✅ " + b.getTitle() + " issued to " + m.getName());
        System.out.println("📅 Due Date: " + LocalDate.now().plusDays(7));
    }

    void returnBook(String title, int id, LocalDate date) {
        for (Transaction t : transactions) {
            if (t.getBook().getTitle().equalsIgnoreCase(title)
                    && t.getMember().getMemberId() == id) {
                t.returnBook(date);
                System.out.println("🔄 Returned: " + title + " | Fine: ₹" + t.calculateFine());
                return;
            }
        }
        System.out.println("❌ No record found for this book!");
    }

    void showBooks() {
        System.out.println("\n📚 Library Books:");
        for (Book b : books) {
            LibraryItem item = b; // Polymorphism
            System.out.println(item.getDetails() +
                    (b.isIssued() ? " [Issued]" : " [Available]"));
        }
    }

    void showMembers() {
        System.out.println("\n👥 Library Members:");
        for (Member m : members) System.out.println(m);
    }

    void showTransactions() {
        System.out.println("\n📝 Transactions:");
        for (Transaction t : transactions) System.out.println(t);
    }
}

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        Library lib = new Library();
        DateTimeFormatter f = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        int ch;

        do {
            System.out.println("\n===== 📚 Library Menu =====");
            System.out.println("1.Add Book\n2.Add Member\n3.Show Books\n4.Show Members");
            System.out.println("5.Issue Book\n6.Return Book\n7.Show Transactions\n8.Exit");
            System.out.print("👉 Enter your choice: ");
            ch = sc.nextInt();
            sc.nextLine();

            switch (ch) {
                case 1 -> {
                    System.out.print("Enter Book Title: ");
                    String t = sc.nextLine();
                    System.out.print("Enter Author: ");
                    String a = sc.nextLine();
                    lib.addBook(t, a);
                }
                case 2 -> {
                    System.out.print("Enter Member Name: ");
                    String n = sc.nextLine();
                    System.out.print("Enter Member ID: ");
                    int id = sc.nextInt();
                    lib.addMember(n, id);
                }
                case 3 -> lib.showBooks();
                case 4 -> lib.showMembers();
                case 5 -> {
                    System.out.print("Enter Book Title: ");
                    String t = sc.nextLine();
                    System.out.print("Enter Member ID: ");
                    int id = sc.nextInt();
                    lib.issueBook(t, id);
                }
                case 6 -> {
                    System.out.print("Enter Book Title: ");
                    String t = sc.nextLine();
                    System.out.print("Enter Member ID: ");
                    int id = sc.nextInt();
                    sc.nextLine();
                    System.out.print("Enter Return Date (yyyy-MM-dd): ");
                    LocalDate d = LocalDate.parse(sc.nextLine(), f);
                    lib.returnBook(t, id, d);
                }
                case 7 -> lib.showTransactions();
                case 8 -> System.out.println("👋 Exiting Library System...");
                default -> System.out.println("❌ Invalid choice!");
            }
        } while (ch != 8);

        sc.close();
    }
}