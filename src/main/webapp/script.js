 function fetch_auth_info(){
    fetch('/AuthServlet').then(response => response.json()).then((logUrl) => {
    console.log(logUrl);
    document.getElementById("loglink").href = logUrl[1];
    document.getElementById("loglink").innerText = logUrl[0];
     });
 }
 