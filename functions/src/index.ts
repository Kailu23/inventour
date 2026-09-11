import {onDocumentWritten} from "firebase-functions/v2/firestore";
import * as admin from "firebase-admin";
import {setGlobalOptions} from "firebase-functions";

admin.initializeApp();

setGlobalOptions({maxInstances: 10});

/**
 * Triggered on any change to the 'products' collection.
 * Sends notifications for Create, Update, and Delete actions.
 * Also checks warehouse occupancy thresholds.
 */
export const handleProductChange =
    onDocumentWritten("products/{productId}", async (event) => {
      console.log(
        "handleProductChange triggered for productId:",
        event.params.productId);

      const beforeData = event.data?.before.data();
      const afterData = event.data?.after.data();

      let action = "";
      let productName = "";
      let userId = "";

      if (!beforeData && afterData) {
        action = "ADD";
        productName = afterData.name;
        userId = afterData.userId;
      } else if (beforeData && afterData) {
        action = "UPDATE";
        productName = afterData.name;
        userId = afterData.userId;
      } else if (beforeData && !afterData) {
        action = "DELETE";
        productName = beforeData.name;
        userId = beforeData.userId;
      }

      console.log(
        `Action: ${action}, Product: ${productName}, User: ${userId}`
      );

      if (!userId) {
        console.log("No userId found, skipping notification.");
        return;
      }

      const changeMessage: admin.messaging.Message = {
        notification: {
          title: action === "ADD" ?
            "Novi proizvod" :
            (action === "UPDATE" ? "Proizvod ažuriran" : "Proizvod obrisan"),
          body: `Proizvod "${productName}" je uspješno ${action === "ADD" ?
            "dodan" :
            (action === "UPDATE" ? "ažuriran" : "uklonjen")}.`,
        },
        data: {
          title: action === "ADD" ?
            "Novi proizvod" :
            (action === "UPDATE" ? "Proizvod ažuriran" : "Proizvod obrisan"),
          body: `Proizvod "${productName}" je uspješno ${action === "ADD" ?
            "dodan" :
            (action === "UPDATE" ? "ažuriran" : "uklonjen")}.`,
          type: "PRODUCT_CHANGE",
          action: action,
          productId: event.params.productId,
        },
        topic: "inventory_updates",
      };

      try {
        const response = await admin.messaging().send(changeMessage);
        console.log("Change notification sent successfully:", response);
      } catch (error) {
        console.error("Error sending change notification:", error);
      }

      // Occupancy Logic
      const capacity = 100;
      try {
        const snapshot =
            await admin.firestore().collection("products").count().get();
        const count = snapshot.data().count;
        const occupancyPercent = Math.floor((count / capacity) * 100);

        console.log(
          `Current occupancy: ${count}/${capacity} (${occupancyPercent}%)`
        );

        const thresholds = [25, 50, 75, 100];
        if (thresholds.includes(occupancyPercent)) {
          const occupancyMessage: admin.messaging.Message = {
            notification: {
              title: "Upozorenje o popunjenosti",
              body: `Skladište je sada na ${occupancyPercent}% kapaciteta.`,
            },
            data: {
              title: "Upozorenje o popunjenosti",
              body: `Skladište je sada na ${occupancyPercent}% kapaciteta.`,
              type: "OCCUPANCY_ALERT",
              percent: occupancyPercent.toString(),
            },
            topic: "inventory_updates",
          };

          const response = await admin.messaging().send(occupancyMessage);
          console.log("Occupancy notification sent successfully:", response);
        }
      } catch (error) {
        console.error(
          "Error calculating occupancy or sending notification:",
          error);
      }
    });
