package main

import (
	"encoding/json"
	"fmt"
	"log"
	"net/http"
)

func enableCors(w http.ResponseWriter) {
	w.Header().Set("Access-Control-Allow-Origin", "*")
	w.Header().Set("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS")
	w.Header().Set("Access-Control-Allow-Headers", "Content-Type, Authorization")
}

func handleConversion(w http.ResponseWriter, r *http.Request) {
	log.Println("POST /api/convert")
	enableCors(w)

	var req ConversionReq
	err := json.NewDecoder(r.Body).Decode(&req)
	if err != nil {
		http.Error(w, "Malformed JSON request body", http.StatusBadRequest)
		log.Fatalln(err.Error())
		return
	}
	log.Printf("REQUEST: %s\n", req.toString())

	var res ConversionRes
	switch req.Measure {
	case "length":
		res = req.convertLength()
	case "weight":
		res = req.convertWeight()
	case "temperature":
		res = req.convertTemperature()
	default:
		http.Error(w, "Unsupported conversion type", http.StatusBadRequest)
		log.Fatalln("Unsupported conversion type")
		return
	}
	log.Printf("RESPONSE: %s, Status %d\n", res.toString(), http.StatusOK)
	w.Header().Set("Content-Type", "application/json")
	w.WriteHeader(http.StatusOK)
	json.NewEncoder(w).Encode(res)
}

func main() {
	mux := http.NewServeMux()

	mux.HandleFunc("POST /api/convert", handleConversion)
	mux.HandleFunc("OPTIONS /api/convert", func(w http.ResponseWriter, r *http.Request) {
		enableCors(w)
		w.WriteHeader(http.StatusOK)
	})

	fmt.Println("Server running on http://localhost:8080")
	log.Fatal(http.ListenAndServe(":8080", mux))
}
