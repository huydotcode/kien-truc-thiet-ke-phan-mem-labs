package task2_1;

interface OrderState {
    void processOrder(Order order);
}

class NewState implements OrderState {
    public void processOrder(Order order) {
        System.out.println("Trạng thái [Mới tạo]: Đang kiểm tra thông tin đơn hàng...");
        order.setState(new ProcessingState());
    }
}

class ProcessingState implements OrderState {
    public void processOrder(Order order) {
        System.out.println("Trạng thái [Đang xử lý]: Đang đóng gói và vận chuyển...");
        order.setState(new DeliveredState());
    }
}

class DeliveredState implements OrderState {
    public void processOrder(Order order) {
        System.out.println("Trạng thái [Đã giao]: Cập nhật trạng thái đơn hàng là đã giao thành công.");
    }
}

class CanceledState implements OrderState {
    public void processOrder(Order order) {
        System.out.println("Trạng thái [Hủy]: Đã hủy đơn hàng và đang tiến hành hoàn tiền.");
    }
}

class Order {
    private OrderState state;

    public Order() {
        this.state = new NewState(); // Mặc định khi tạo Order
    }

    public void setState(OrderState state) {
        this.state = state;
    }

    public void process() {
        state.processOrder(this);
    }

    public void cancel() {
        System.out.println("Yêu cầu hủy đơn hàng...");
        this.state = new CanceledState();
        state.processOrder(this);
    }
}

public class Task2_1 {
    public static void main(String[] args) {
        Order order1 = new Order();
        order1.process(); // Mới tạo -> Kiểm tra -> Chuyển sang Đang xử lý
        order1.process(); // Đang xử lý -> Đóng gói, vận chuyển -> Chuyển sang Đã giao
        order1.process(); // Đã giao

        Order order2 = new Order();
        order2.process(); // Mới tạo
        order2.cancel();  // Hủy
    }
}
