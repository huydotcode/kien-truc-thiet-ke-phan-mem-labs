package task2_2;

interface TaxStrategy {
    double calculateTax(double price);
}

class VATStrategy implements TaxStrategy {
    public double calculateTax(double price) {
        return price * 0.10; // 10% VAT
    }
}

class ConsumerTaxStrategy implements TaxStrategy {
    public double calculateTax(double price) {
        return price * 0.05; // 5% Consumer tax
    }
}

class LuxuryTaxStrategy implements TaxStrategy {
    public double calculateTax(double price) {
        return price * 0.20; // 20% Luxury tax
    }
}

class Product {
    private String name;
    private double price;
    private TaxStrategy taxStrategy;

    public Product(String name, double price, TaxStrategy taxStrategy) {
        this.name = name;
        this.price = price;
        this.taxStrategy = taxStrategy;
    }

    public void setTaxStrategy(TaxStrategy taxStrategy) {
        this.taxStrategy = taxStrategy;
    }

    public double getFinalPrice() {
        double tax = taxStrategy.calculateTax(price);
        return price + tax;
    }

    public void display() {
        System.out.println("Sản phẩm: " + name + " | Giá gốc: " + price + " | Thuế: " + taxStrategy.calculateTax(price) + " | Tổng giá: " + getFinalPrice());
    }
}

public class Task2_2 {
    public static void main(String[] args) {
        Product p1 = new Product("Gạo", 100, new ConsumerTaxStrategy());
        Product p2 = new Product("Điện thoại", 1000, new VATStrategy());
        Product p3 = new Product("Nước hoa đắt tiền", 5000, new LuxuryTaxStrategy());
        
        p1.display();
        p2.display();
        p3.display();
        
        p2.setTaxStrategy(new ConsumerTaxStrategy());
        p2.display();
    }
}
