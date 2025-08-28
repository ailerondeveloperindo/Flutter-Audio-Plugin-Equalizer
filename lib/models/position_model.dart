

import 'dart:ffi';

class PositionStateModel {
  int position;
  int durationMs;

  PositionStateModel({
    this.position = 0,
    this.durationMs = 0,
  });

  factory PositionStateModel.fromJson(Map<String, dynamic> json) {
    return PositionStateModel(
      position: json['position'] ?? 0,
      durationMs: json['durationMs'] ?? 0,
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'position': position,
      'durationMs': durationMs,
    };
  }
}