
import 'dart:ffi';

class DeviceStateModel {
  double volume;
  bool isMuted;

  DeviceStateModel({
    this.volume = 0.0,
    this.isMuted = false,
  });

  factory DeviceStateModel.fromJson(Map<String, dynamic> json) {
    return DeviceStateModel(
      volume: json['volume'] ?? 0,
      isMuted: json['isMuted'] ?? false,
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'volume': volume,
      'isMuted': isMuted,
    };
  }
}