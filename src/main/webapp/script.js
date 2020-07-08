var currentListings = []

class Listing {
  constructor(subject, desc, time) {
    this.subject = subject;
    this.description = desc;
    this.time = time;
  }
}

function loadListings() {
  fetch_auth_info();
  getListings();
}

function fetch_auth_info(){
  fetch('/AuthServlet').then(response => response.json()).then((logUrl) => {
  console.log(logUrl);
  document.getElementById("loglink").href = logUrl[1];
  document.getElementById("loglink").innerText = logUrl[0];
    });
}

/**
  * Fetches comments from datastore, converts them to JSON and then does 2 things:
  * 1) Creates new Listing objects from them and saves them in a global variable
  * 2) updates the DOM to add HTML representing each Listing object.
  */
function getListings() {
  let container = document.getElementById('listings-container');

  fetch('/listings').then(res => res.json())
  .then((fetchedListings) => {
    currentListings = [];

    for (var l of fetchedListings) {
      let newListing = Object.assign(new Listing(), l);
      currentListings.push(newListing);
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

  // Main part of listing (according to wireframe)
  body.appendChild(createParagraphElement(listing.description));

  // Bottom bar of listing.
  let timestampHTML = footer.querySelector('.timestamp');
  timestampHTML.innerText = (new Date(listing.timestamp));

  return listingHTML;
}

/**
 * Given some text, creates a paragraph element;
 */
function createParagraphElement(text) {
  const pElement = document.createElement('p');
  pElement.innerText = text;
  return pElement;
}

function createButtonElement(text) {
  const button = document.createElement('p');
  button.innerText = text;
  return button;
}
 