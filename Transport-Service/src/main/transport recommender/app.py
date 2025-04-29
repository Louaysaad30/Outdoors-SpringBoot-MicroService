from flask import Flask, request, jsonify
from flask_cors import CORS
from recommender import VehicleRecommender
import os

app = Flask(__name__)
CORS(app)  # Enable CORS for cross-origin requests
recommender = VehicleRecommender(quantize=True)

@app.route("/recommend", methods=["POST"])
def recommend():
    try:
        data = request.get_json()
        mood_input = data.get("mood_input", "")
        vehicle_data = data.get("vehicules", [])

        # Ensure all vehicles have required fields
        for v in vehicle_data:
            v['prixParJour'] = v.get('prixParJour', 0)  # Default to 0 if missing
            v['rating'] = v.get('rating', 0)  # Default to 0 if missing

        result = recommender.recommend_from_list(mood_input, vehicle_data)
        return jsonify(result)
    except Exception as e:
        return jsonify({"error": str(e)}), 500

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5006, debug=True)