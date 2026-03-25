interface TaskObserver {
  update(task: Task): void;
}

interface TaskSubject {
  attach(observer: TaskObserver): void;
  detach(observer: TaskObserver): void;
  notify(): void;
}

class Task implements TaskSubject {
  private observers: TaskObserver[] = [];

  constructor(
    public name: string,
    private status: string,
  ) {}

  attach(observer: TaskObserver): void {
    if (!this.observers.includes(observer)) {
      this.observers.push(observer);
    }
  }

  detach(observer: TaskObserver): void {
    this.observers = this.observers.filter((obs) => obs !== observer);
  }

  notify(): void {
    console.log(`\n[Task Management] Notifying team about task '${this.name}' status change...`);
    for (const observer of this.observers) {
      observer.update(this);
    }
  }

  setStatus(newStatus: string): void {
    console.log(`\n[Task Management] Task '${this.name}' status changed to '${newStatus}'`);
    this.status = newStatus;
    this.notify();
  }

  getStatus(): string {
    return this.status;
  }
}

class TeamMember implements TaskObserver {
  constructor(
    private name: string,
    private role: string,
  ) {}

  update(task: Task): void {
    console.log(`  -> ${this.role} ${this.name} notified: Task '${task.name}' is now [${task.getStatus()}]`);
  }
}

// === Client Code ===
function testTaskManagement() {
  console.log("\n--- Testing Task Management (Observer Pattern) ---");

  const designTask = new Task("Design Database Schema", "To Do");

  const managerAlice = new TeamMember("Alice", "Project Manager");
  const devBob = new TeamMember("Bob", "Backend Developer");
  const qaCharlie = new TeamMember("Charlie", "QA Engineer");

  designTask.attach(managerAlice);
  designTask.attach(devBob);

  designTask.setStatus("In Progress");

  designTask.attach(qaCharlie);

  designTask.setStatus("Done");
}

testTaskManagement();
