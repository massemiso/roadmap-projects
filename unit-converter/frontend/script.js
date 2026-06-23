const lengthForm = document.getElementById("length-form");
const weightForm = document.getElementById("weight-form");
const temperatureForm = document.getElementById("temperature-form");

lengthForm.addEventListener("submit", (event) => postConvertLength(event));
weightForm.addEventListener("submit", (event) => postConvertWeight(event));
temperatureForm.addEventListener("submit", (event) =>
  postConvertTemperature(event),
);

function postConvert(event, form, measure) {
  event.preventDefault();

  const conversion = {
    measure: measure,
    value: parseFloat(form.elements[measure + "-value"].value),
    from: form.elements[measure + "-from"].value,
    to: form.elements[measure + "-to"].value,
  };

  fetch("http://localhost:8080/api/convert", {
    method: "POST",
    headers: {
      Accept: "application/json, text/plain, */*",
      "Content-Type": "application/json",
    },
    body: JSON.stringify(conversion),
  })
    .then((res) => {
      if (!res.ok) {
        throw new Error(`Server returned status code: ${res.status}`);
      }
      return res.json();
    })
    .then((data) => {
      document.getElementById(measure + "-result").textContent =
        `${data.result.toFixed(2)} ${conversion.to}`;
    })
    .catch(() => {
      document.getElementById(measure + "-result").textContent =
        `Invalid conversion from ${conversion.to} to ${conversion.from}`;
    });
}

function postConvertLength(event) {
  postConvert(event, lengthForm, "length");
}

function postConvertWeight(event) {
  postConvert(event, weightForm, "weight");
}

function postConvertTemperature(event) {
  postConvert(event, temperatureForm, "temperature");
}
