interface Observer {
  update(stock: Stock): void;
}

interface Subject {
  attach(observer: Observer): void;
  detach(observer: Observer): void;
  notify(): void;
}

class Stock implements Subject {
  private observers: Observer[] = [];

  constructor(
    public symbol: string,
    private price: number,
  ) {}

  attach(observer: Observer): void {
    if (!this.observers.includes(observer)) {
      this.observers.push(observer);
    }
  }

  detach(observer: Observer): void {
    this.observers = this.observers.filter((obs) => obs !== observer);
  }

  notify(): void {
    console.log(`\n[Stock Market] Notifying observers about ${this.symbol} price change...`);
    for (const observer of this.observers) {
      observer.update(this);
    }
  }

  setPrice(newPrice: number): void {
    console.log(`\n[Stock Market] ${this.symbol} price changed from $${this.price} to $${newPrice}`);
    this.price = newPrice;
    this.notify();
  }

  getPrice(): number {
    return this.price;
  }
}

class Investor implements Observer {
  constructor(private name: string) {}

  update(stock: Stock): void {
    console.log(`  -> Investor ${this.name} received update: ${stock.symbol} is now $${stock.getPrice()}`);
  }
}

// === Client Code ===
function testStockMarket() {
  console.log("--- Testing Stock Market (Observer Pattern) ---");

  const appleStock = new Stock("AAPL", 150);

  const investorJohn = new Investor("John Doe");
  const investorJane = new Investor("Jane Smith");
  const investorFund = new Investor("Global Tech Fund");

  appleStock.attach(investorJohn);
  appleStock.attach(investorJane);

  appleStock.setPrice(155);

  appleStock.attach(investorFund);
  appleStock.detach(investorJohn);

  appleStock.setPrice(160);
}

testStockMarket();
