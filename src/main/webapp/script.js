let currentListings = [];
let currentUser;


function loadListings() {
  fetch_auth_info();
  getListings();
}

// function loadListings() {
//   fetch_auth_info().then((res) => {getListings();});
// }

/**
  * Fetches comments from datastore, converts them to JSON and then does 2 things:
  * 1) Creates new Listing objects from them and saves them in a global variable
  * 2) updates the DOM to add HTML representing each Listing object.
  */
function getListings() {
  let container = document.getElementById('listings-container');

  fetch('/listings').then(res => res.json())
  .then((fetchedListings) => {
    currentListings = fetchedListings;

    for (var newListing of fetchedListings) {
      container.appendChild(createDOMListing(newListing));
    }
  });
}

/**
 * Gets the HTML template for a Listing and populates it with data from the given Listing input
 */
function createDOMListing(listing) {
  // Get hidden Listing structure
  const listingHTML = document.getElementById('listing-template').cloneNode(true);

  // Remove template-unique attributes
  listingHTML.removeAttribute('id');
  listingHTML.removeAttribute('hidden');

  // Get two main parts of the Listing
  let header = listingHTML.querySelector('.listing-header');
  let body = listingHTML.querySelector('.listing-body');
  let footer = listingHTML.querySelector('.listing-footer');

  // Populate fields
  // Top bar of listing (according to wireframe)
  header.appendChild(createParagraphElement(listing.subject));
  header.appendChild(createParagraphElement(listing.email));

  // Main part of listing (according to wireframe)
  body.appendChild(createParagraphElement(listing.description));

  // Bottom bar of listing.
  let timestampHTML = footer.querySelector('.timestamp');
  timestampHTML.innerText = (new Date(listing.timestamp));

  return listingHTML;
}

/**
 * Loads some hidden HTML fields with user data to pass to backend datastore
 */
function loadFormData() {
  fetch_auth_info().then((res) => {
    emailInput = document.querySelector('input[name="email"]');
    userIdInput = document.querySelector('input[name="userId"]');

    if (currentUser.info) {
      emailInput.setAttribute('value', currentUser.info.email);
      userIdInput.setAttribute('value', currentUser.info.userId);
    }
  });
}

/**
 * Given some text, creates a paragraph element;
 */
function createParagraphElement(text) {
  const pElement = document.createElement('p');
  pElement.innerText = text;
  return pElement;
}

//   Adjusts links on the nav bar based on whether the user is logged in
function fetch_auth_info(){
  let currentUrl = window.location.href;
  let currentPage = currentUrl.substring(currentUrl.lastIndexOf('/'));
  currentPage = currentPage === '/?authuser=0' ? '/index.html' : currentPage;
  const params = new URLSearchParams();
  params.append('currentPage', currentPage);

  console.log(currentPage);
  fetch('/AuthServlet', {
    method: 'POST',
    body: params
  }).then(response => response.json()).then((userData) => {
  currentUser = userData;
  console.log(userData);
  if (userData.loginUrl) {
    if (currentPage === '/index.html') {
      document.getElementById("loglink").innerText = "Login";
      document.getElementById("loglink").href = userData.loginUrl;
    }
    else {
      window.location.replace(userData.loginUrl);
    }
  }
  else {
    document.getElementById("loglink").innerText = "Logout";
    document.getElementById("loglink").href = userData.logoutUrl;
  }
  });
}
 