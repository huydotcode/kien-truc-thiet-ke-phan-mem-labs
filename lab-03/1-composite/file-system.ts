interface FileSystemComponent {
  display(indent: string): void;
}

class FileComponent implements FileSystemComponent {
  constructor(
    private name: string,
    private size: number,
  ) {}

  display(indent: string): void {
    console.log(`${indent}- 📄 [File] ${this.name} (${this.size} KB)`);
  }
}

class Directory implements FileSystemComponent {
  private children: FileSystemComponent[] = [];

  constructor(private name: string) {}

  add(component: FileSystemComponent): void {
    this.children.push(component);
  }

  remove(component: FileSystemComponent): void {
    this.children = this.children.filter((c) => c !== component);
  }

  display(indent: string): void {
    console.log(`${indent}+ 📁 [Directory] ${this.name}`);
    for (const child of this.children) {
      child.display(indent + "  ");
    }
  }
}

// === Client Code ===
function testFileSystem() {
  console.log("--- Testing File System (Composite Pattern) ---");

  const root = new Directory("root");

  const docs = new Directory("documents");
  const pics = new Directory("pictures");

  const file1 = new FileComponent("resume.pdf", 1200);
  const file2 = new FileComponent("budget.xlsx", 500);
  docs.add(file1);
  docs.add(file2);

  const pic1 = new FileComponent("vacation.jpg", 3500);
  pics.add(pic1);

  root.add(docs);
  root.add(pics);

  const looseFile = new FileComponent("readme.txt", 15);
  root.add(looseFile);

  root.display("");
}

testFileSystem();
