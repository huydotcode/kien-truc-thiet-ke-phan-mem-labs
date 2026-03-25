// 2. Factory Method Pattern: Book Types
interface Book {
  title: string;
  author: string;
  category: string;
  type: string;
  getInfo(): string;
}

class PaperBook implements Book {
  constructor(
    public title: string,
    public author: string,
    public category: string,
    public type: string = "Paper",
  ) {}
  getInfo() {
    return `[PaperBook] ${this.title} by ${this.author} (${this.category})`;
  }
}

class EBook implements Book {
  constructor(
    public title: string,
    public author: string,
    public category: string,
    public type: string = "EBook",
  ) {}
  getInfo() {
    return `[EBook] ${this.title} by ${this.author} (${this.category})`;
  }
}

class AudioBook implements Book {
  constructor(
    public title: string,
    public author: string,
    public category: string,
    public type: string = "Audio",
  ) {}
  getInfo() {
    return `[AudioBook] ${this.title} by ${this.author} (${this.category})`;
  }
}

class BookFactory {
  createBook(type: "Paper" | "EBook" | "Audio", title: string, author: string, category: string): Book {
    switch (type) {
      case "Paper":
        return new PaperBook(title, author, category);
      case "EBook":
        return new EBook(title, author, category);
      case "Audio":
        return new AudioBook(title, author, category);
      default:
        throw new Error(`Unknown book type: ${type}`);
    }
  }
}

// 3. Strategy Pattern: Search Algorithms
interface SearchStrategy {
  search(books: Book[], query: string): Book[];
}

class SearchByName implements SearchStrategy {
  search(books: Book[], query: string): Book[] {
    return books.filter((b) => b.title.toLowerCase().includes(query.toLowerCase()));
  }
}

class SearchByAuthor implements SearchStrategy {
  search(books: Book[], query: string): Book[] {
    return books.filter((b) => b.author.toLowerCase().includes(query.toLowerCase()));
  }
}

class SearchByCategory implements SearchStrategy {
  search(books: Book[], query: string): Book[] {
    return books.filter((b) => b.category.toLowerCase().includes(query.toLowerCase()));
  }
}

// 4. Observer Pattern: Notifications
interface Observer {
  update(message: string): void;
}

class LibraryMember implements Observer {
  constructor(private name: string) {}
  update(message: string): void {
    console.log(`[Member ${this.name}] Received Notification: ${message}`);
  }
}

class Librarian implements Observer {
  constructor(private name: string) {}
  update(message: string): void {
    console.log(`[Librarian ${this.name}] System Alert: ${message}`);
  }
}

// 1. Singleton Pattern: Library Manager
class Library {
  private static instance: Library;
  private books: Book[] = [];
  private observers: Observer[] = [];
  private searchStrategy: SearchStrategy = new SearchByName(); // Default strategy

  private constructor() {}

  public static getInstance(): Library {
    if (!Library.instance) {
      Library.instance = new Library();
    }
    return Library.instance;
  }

  // Observer Management
  subscribe(observer: Observer) {
    this.observers.push(observer);
  }

  unsubscribe(observer: Observer) {
    this.observers = this.observers.filter((obs) => obs !== observer);
  }

  notifyObservers(message: string) {
    for (const observer of this.observers) {
      observer.update(message);
    }
  }

  // Book Management
  addBook(book: Book) {
    this.books.push(book);
    this.notifyObservers(`New book available in the library: "${book.title}" by ${book.author}`);
  }

  getBooks(): Book[] {
    return this.books;
  }

  // Search Management
  setSearchStrategy(strategy: SearchStrategy) {
    this.searchStrategy = strategy;
  }

  searchBooks(query: string): Book[] {
    return this.searchStrategy.search(this.books, query);
  }
}

// 5. Decorator Pattern: Borrowing Features
interface Borrowing {
  borrow(): string;
}

class BasicBorrowing implements Borrowing {
  constructor(
    private book: Book,
    private user: string,
  ) {}
  borrow(): string {
    return `${this.user} borrowed "${this.book.title}" for standard 14 days.`;
  }
}

abstract class BorrowingDecorator implements Borrowing {
  constructor(protected wrappedBorrowing: Borrowing) {}
  borrow(): string {
    return this.wrappedBorrowing.borrow();
  }
}

class ExtendedTimeDecorator extends BorrowingDecorator {
  borrow(): string {
    return super.borrow() + ` [Feature: Extended to 30 days]`;
  }
}

class BrailleDecorator extends BorrowingDecorator {
  borrow(): string {
    return super.borrow() + ` [Feature: Braille Edition]`;
  }
}

class TranslatedDecorator extends BorrowingDecorator {
  constructor(
    wrappedBorrowing: Borrowing,
    private language: string,
  ) {
    super(wrappedBorrowing);
  }
  borrow(): string {
    return super.borrow() + ` [Feature: Translated to ${this.language}]`;
  }
}

function runLibrarySystem() {
  console.log("\n--- Initializing Library System ---");
  const library = Library.getInstance();

  // 1. Observers setup
  const alice = new LibraryMember("Alice");
  const bob = new LibraryMember("Bob");
  const adminJane = new Librarian("Jane");

  library.subscribe(alice);
  library.subscribe(bob);
  library.subscribe(adminJane);

  // 2. Factory creation & Adding Books
  const factory = new BookFactory();
  console.log("\n--- Adding Books to Library ---");
  library.addBook(factory.createBook("Paper", "Design Patterns: Elements of Reusable Object-Oriented Software", "GoF", "Technology"));
  library.addBook(factory.createBook("EBook", "Clean Code", "Robert C. Martin", "Technology"));
  library.addBook(factory.createBook("Audio", "The Pragmatic Programmer", "Andrew Hunt", "Technology"));
  library.addBook(factory.createBook("Paper", "Harry Potter", "J.K. Rowling", "Fantasy"));

  // 3. Decorator pattern for borrowing
  console.log("\n--- Borrowing Books (Decorator) ---");
  const hpBook = library.searchBooks("Harry Potter")[0];

  if (!hpBook) {
    console.error("Book not found!");
    return;
  }

  // Alice borrows plain Harry Potter
  const aliceBorrow = new BasicBorrowing(hpBook, "Alice");
  console.log(aliceBorrow.borrow());

  // Bob borrows Harry Potter with Extended Time and Braille
  let bobBorrow: Borrowing = new BasicBorrowing(hpBook, "Bob");
  bobBorrow = new ExtendedTimeDecorator(bobBorrow);
  bobBorrow = new BrailleDecorator(bobBorrow);
  console.log(bobBorrow.borrow());

  // Charlie needs a translation to Vietnamese
  let charlieBorrow: Borrowing = new BasicBorrowing(hpBook, "Charlie");
  charlieBorrow = new TranslatedDecorator(charlieBorrow, "Vietnamese");
  console.log(charlieBorrow.borrow());

  // 4. Searching with Strategies
  console.log("\n--- Searching Books (Strategy) ---");
  // Using default ByName search
  console.log('Search by Title "Clean":');
  console.log(library.searchBooks("Clean").map((b) => b.getInfo()));

  // Switching strategy
  console.log("--- Switching Search Strategy to ByAuthor ---");
  library.setSearchStrategy(new SearchByAuthor());
  console.log('Search by Author "GoF":');
  console.log(library.searchBooks("GoF").map((b) => b.getInfo()));

  console.log("--- Switching Search Strategy to ByCategory ---");
  library.setSearchStrategy(new SearchByCategory());
  console.log('Search by Category "Technology":');
  console.log(library.searchBooks("Technology").map((b) => b.getInfo()));
}

// Execute the simulation
runLibrarySystem();
