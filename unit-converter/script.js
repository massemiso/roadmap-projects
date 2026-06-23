const lengthForm = document.getElementById("length-form");
const weightForm = document.getElementById("weight-form");
const temperatureForm = document.getElementById("temperature-form");

lengthForm.addEventListener("submit", (event) => convertLength(event));
weightForm.addEventListener("submit", (event) => convertWeight(event));
temperatureForm.addEventListener("submit", (event) =>
  convertTemperature(event),
);

function convertAnything(event, from, to, form, measure) {
  event.preventDefault();
  const value = parseFloat(form.elements[measure + "-value"].value);
  const unitFrom = form.elements[measure + "-from"].value;
  const unitTo = form.elements[measure + "-to"].value;

  if (unitTo == unitFrom) {
    document.getElementById(measure + "-result").textContent =
      `${value.toFixed(2)} ${unitTo}`;
    return;
  }

  var valueSI = from[unitFrom](value);
  var valueTo = to[unitTo](valueSI);

  document.getElementById(measure + "-result").textContent =
    `${valueTo.toFixed(2)} ${unitTo}`;
}

function convertLength(event) {
  var fromAnyToSI = {
    mm: (val) => val / 1000,
    cm: (val) => val / 100,
    m: (val) => val,
    km: (val) => val * 1000,
    in: (val) => val / 39.37,
    ft: (val) => val / 3.281,
    yd: (val) => val / 1.094,
    mi: (val) => val * 1609,
  };
  var fromSIToAny = {
    mm: (val) => val * 1000,
    cm: (val) => val * 100,
    m: (val) => val,
    km: (val) => val / 1000,
    in: (val) => val * 39.37,
    ft: (val) => val * 3.281,
    yd: (val) => val * 1.094,
    mi: (val) => val / 1609,
  };
  convertAnything(event, fromAnyToSI, fromSIToAny, lengthForm, "length");
}

function convertWeight(event) {
  var fromAnyToSI = {
    mg: (val) => val / 1_000_000,
    g: (val) => val / 1000,
    kg: (val) => val,
    oz: (val) => val / 35.274,
    lb: (val) => val / 2.205,
  };
  var fromSIToAny = {
    mg: (val) => val * 1_000_000,
    g: (val) => val * 1000,
    kg: (val) => val,
    oz: (val) => val * 35.274,
    lb: (val) => val * 2.205,
  };
  convertAnything(event, fromAnyToSI, fromSIToAny, weightForm, "weight");
}

function convertTemperature(event) {
  var fromAnyToSI = {
    C: (val) => val + 273.15,
    F: (val) => (val - 32) * (5 / 9) + 273.15,
    K: (val) => val,
  };
  var fromSIToAny = {
    C: (val) => val - 273.15,
    F: (val) => (val - 273.15) * (9 / 5) + 32,
    K: (val) => val,
  };
  convertAnything(
    event,
    fromAnyToSI,
    fromSIToAny,
    temperatureForm,
    "temperature",
  );
}
