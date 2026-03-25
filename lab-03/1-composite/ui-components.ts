interface UIComponent {
  render(indent: string): void;
}

class Button implements UIComponent {
  constructor(private label: string) {}

  render(indent: string): void {
    console.log(`${indent}- 🔲 [Button] "${this.label}"`);
  }
}

class NavigationBar implements UIComponent {
  constructor(private items: string[]) {}

  render(indent: string): void {
    console.log(`${indent}- 🧭 [Navigation] Items: ${this.items.join(", ")}`);
  }
}

class UIContainer implements UIComponent {
  private children: UIComponent[] = [];

  constructor(private name: string) {}

  add(component: UIComponent): void {
    this.children.push(component);
  }

  remove(component: UIComponent): void {
    this.children = this.children.filter((c) => c !== component);
  }

  render(indent: string): void {
    console.log(`${indent}+ 📦 [Container] ${this.name}`);
    for (const child of this.children) {
      child.render(indent + "  ");
    }
  }
}

// === Client Code ===
function testUIComponents() {
  console.log("\n--- Testing UI Components (Composite Pattern) ---");

  const mainWindow = new UIContainer("Main Window");

  const header = new UIContainer("Header");
  const navBar = new NavigationBar(["Home", "About", "Contact"]);
  header.add(navBar);

  const dialogBox = new UIContainer("Login Dialog");
  const btnLogin = new Button("Login");
  const btnCancel = new Button("Cancel");
  dialogBox.add(btnLogin);
  dialogBox.add(btnCancel);

  const looseButton = new Button("Help");

  mainWindow.add(header);
  mainWindow.add(dialogBox);
  mainWindow.add(looseButton);

  mainWindow.render("");
}

testUIComponents();
