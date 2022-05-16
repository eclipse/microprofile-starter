package [# th:text="${java_package}"/].openapi;

import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.info.Info;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;

import [# th:text="${jakarta_ee_package}"/].enterprise.context.ApplicationScoped;
import [# th:text="${jakarta_ee_package}"/].ws.rs.GET;
import [# th:text="${jakarta_ee_package}"/].ws.rs.Path;
import [# th:text="${jakarta_ee_package}"/].ws.rs.PathParam;
import [# th:text="${jakarta_ee_package}"/].ws.rs.Produces;
import [# th:text="${jakarta_ee_package}"/].ws.rs.core.MediaType;
import [# th:text="${jakarta_ee_package}"/].ws.rs.core.Response;

@Path("/booking")
@ApplicationScoped
@OpenAPIDefinition(info = @Info(title = "Booking endpoint", version = "1.0"))
public class BookingController {

    @APIResponses(value = {
            @APIResponse(
                    responseCode = "200",
                    description = "Booking for id",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON,
                            schema = @Schema(
                                    ref = "Booking"))
            ),
            @APIResponse(
                    responseCode = "404",
                    description = "No booking found for the id.")
    })
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{bookingId}")
    public Response getBooking(@PathParam("bookingId") String bookingId) {
        return Response
                .status(Response.Status.OK)
                .entity(Booking.booking(bookingId, Destination.destination("New Rearendia", "Wheeli")))
                .build();
    }

}
