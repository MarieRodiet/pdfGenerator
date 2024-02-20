# PDF Generator Service

This webservice is designed to generate a PDF document based on a JSON object provided in the request. It utilizes Thymeleaf templates to structure the content of the PDF, allowing for dynamic data insertion.

## Usage

### Endpoint

`POST http://localhost:7890/letter`

### Request

Send a JSON object with the following structure:

```json
{
  "showLogoAndFooter": true,
  "imgOddPage": false,
  "addressStamped": true,
  "paginationStamped" :true,
  "date" : "mercredi 20 décembre 2020",
  "prefix": "Mr",
  "name": "DOE JOHN",
  "adress": ["1 RUE DE LA PAIX", "99999 SPECIMEN", "Paris 75000"]
}

```
### Response:

The response will be the generated PDF document containing the provided data in the specified format.

### Customization
You can customize the PDF generation process by modifying the Thymeleaf templates and CSS stylesheets located in the server. Adjust the layout, styling, and content as per your requirements.

### Request Attributes

- **showHeaderAndFooter** (boolean): Indicates whether to include header and footer in the generated PDF.
- **imgOddPage** (boolean): Specifies whether to include a special image for odd pages.
- **isAddressStamped** (boolean): Determines whether the address is stamped.
- **date** (string): Date string to be displayed in the document.
- **civiliteMembre** (string): Salutation or title of the person.
- **nomPrenom** (string): Full name of the person.
- **adresse** (array of strings): Address information, formatted as an array of lines.

