package main

import (
	"encoding/json"
	"fmt"
	"log"
	"net/http"
)

func handleConversion(w http.ResponseWriter, r *http.Request) {
	var req ConversionReq
	err := json.NewDecoder(r.Body).Decode(&req)
	if err != nil {
		http.Error(w, "Malformed JSON request body", http.StatusBadRequest)
		log.Fatalln(err.Error())
		return
	}

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
		return
	}

	w.Header().Set("Content-Type", "application/json")
	w.WriteHeader(http.StatusOK)
	json.NewEncoder(w).Encode(res)
}

func main() {
	mux := http.NewServeMux()

	mux.HandleFunc("POST /api/convert", handleConversion)

	fmt.Println("Server running on http://localhost:8080")
	log.Fatal(http.ListenAndServe(":8080", mux))
}
