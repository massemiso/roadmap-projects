package main

import "fmt"

type ConversionReq struct {
	Measure string  `json:"measure"`
	Value   float64 `json:"value"`
	From    string  `json:"from"`
	To      string  `json:"to"`
}

type ConversionRes struct {
	Result float64 `json:"result"`
}

func (req *ConversionReq) convertLength() ConversionRes {
	toMeters := map[string]float64{
		"mm": 0.001, "cm": 0.01, "m": 1.00, "km": 1000.0,
		"in": 0.0254, "ft": 0.3048, "yd": 0.9144, "mi": 1609.34,
	}
	meters := req.Value * toMeters[req.From]
	return ConversionRes{Result: meters / toMeters[req.To]}
}

func (req *ConversionReq) convertWeight() ConversionRes {
	toGrams := map[string]float64{
		"mg": 0.001, "g": 1.0, "kg": 1000.0, "oz": 28.3495, "lb": 453.592,
	}
	grams := req.Value * toGrams[req.From]
	return ConversionRes{Result: grams / toGrams[req.To]}
}

func (req *ConversionReq) convertTemperature() ConversionRes {
	var result float64
	if req.From == req.To {
		return ConversionRes{Result: req.Value}
	}
	var celsius float64
	switch req.From {
	case "C":
		celsius = req.Value
	case "F":
		celsius = (req.Value - 32) * 5 / 9
	case "K":
		celsius = req.Value - 273.15
	}
	switch req.To {
	case "C":
		result = celsius
	case "F":
		result = (celsius * 9 / 5) + 32
	case "K":
		result = celsius + 273.15
	default:
		result = req.Value
	}
	return ConversionRes{Result: result}
}

func (req *ConversionReq) toString() string {
	return fmt.Sprintf("ConversionReq{Measure='%s', Value=%.2f, From='%s', To='%s'}",
		req.Measure, req.Value, req.From, req.To)
}

func (res *ConversionRes) toString() string {
	return fmt.Sprintf("ConversionRes{Result=%.2f}", res.Result)
}
