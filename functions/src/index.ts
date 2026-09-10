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
      const productId = event.params.productId;
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

      if (!userId) return;

      const changeMessage = {
        notification: {
          title: action === "ADD" ?
            "Novi proizvod" :
            (action === "UPDATE" ? "Proizvod ažuriran" : "Proizvod obrisan"),
          body: `Proizvod "${productName}" je uspješno ${action === "ADD" ?
            "dodan" :
            (action === "UPDATE" ? "ažuriran" : "uklonjen")}.`,
        },
        data: {
          type: "PRODUCT_CHANGE",
          action: action,
          productId: productId,
        },
        topic: `user_${userId}`,
      };

      try {
        await admin.messaging().send(changeMessage);
      } catch (error) {
        console.error("Error sending change notification:", error);
      }

      const capacity = 100;
      const snapshot =
          await admin.firestore().collection("products").count().get();
      const count = snapshot.data().count;
      const occupancyPercent = (count / capacity) * 100;

      const thresholds = [25, 50, 75, 100];
      if (thresholds.includes(occupancyPercent)) {
        const occupancyMessage = {
          notification: {
            title: "Upozorenje o popunjenosti",
            body: `Skladište je sada na ${occupancyPercent}% kapaciteta.`,
          },
          data: {
            type: "OCCUPANCY_ALERT",
            percent: occupancyPercent.toString(),
          },
          topic: `user_${userId}`,
        };

        try {
          await admin.messaging().send(occupancyMessage);
        } catch (error) {
          console.error("Error sending occupancy notification:", error);
        }
      }
    });
