package main

import (
	"fmt"
	"net/http"
)

func helloHandler(w http.ResponseWriter, r *http.Request) {
	fmt.Fprintf(w, "Hello, Docker Go!")
}

func main() {
	http.HandleFunc("/", helloHandler)
	fmt.Println("Server is running on port 8080...")
	// Khởi chạy server tại cổng 8080
	if err := http.ListenAndServe(":8080", nil); err != nil {
		fmt.Printf("Error starting server: %s\n", err)
	}
}
